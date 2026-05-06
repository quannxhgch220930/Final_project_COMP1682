package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.enums.PaymentMethod;
import com.ecommerce.backend.entity.enums.PaymentStatus;
import com.ecommerce.backend.exception.AppException;
import com.ecommerce.backend.exception.ErrorCode;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.service.Interface.VnpayPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class VnpayPaymentServiceImpl implements VnpayPaymentService {

    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VNPAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderRepository orderRepository;

    @Value("${vnpay.pay-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String payUrl;

    @Value("${vnpay.tmn-code:}")
    private String tmnCode;

    @Value("${vnpay.hash-secret:}")
    private String hashSecret;

    @Value("${vnpay.return-url:http://localhost:5173/payment/vnpay-return}")
    private String returnUrl;

    @Override
    @Transactional
    public String createPaymentUrl(Long userId, Long orderId, HttpServletRequest request) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new AppException(ErrorCode.ORDER_ALREADY_PAID);
        }
        if (order.getPaymentMethod() != PaymentMethod.VNPAY) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        String txnRef = order.getId() + "-" + System.currentTimeMillis();
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setVnpTxnRef(txnRef);
        orderRepository.save(order);

        LocalDateTime now = LocalDateTime.now(VIETNAM_ZONE);
        LocalDateTime expireAt = now.plusMinutes(15);

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", toVnpayAmount(order.getTotal()));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getId());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", getClientIp(request));
        params.put("vnp_CreateDate", now.format(VNPAY_DATE_FORMAT));
        params.put("vnp_ExpireDate", expireAt.format(VNPAY_DATE_FORMAT));

        String query = buildQuery(params);
        String secureHash = hmacSHA512(hashSecret, buildHashData(params));
        return payUrl + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    @Override
    @Transactional
    public Map<String, String> handleIpn(Map<String, String> params) {
        if (!verifySignature(params)) {
            return ipnResponse("97", "Invalid Checksum");
        }

        String txnRef = params.get("vnp_TxnRef");
        Order order = orderRepository.findByVnpTxnRef(txnRef).orElse(null);
        if (order == null) {
            return ipnResponse("01", "Order not found");
        }

        String vnpAmount = params.get("vnp_Amount");
        if (!toVnpayAmount(order.getTotal()).equals(vnpAmount)) {
            return ipnResponse("04", "Invalid Amount");
        }

        if (order.getPaymentStatus() != PaymentStatus.UNPAID) {
            return ipnResponse("02", "Order already confirmed");
        }

        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        order.setVnpTransactionNo(params.get("vnp_TransactionNo"));
        order.setPaymentStatus(
                "00".equals(responseCode) && "00".equals(transactionStatus)
                        ? PaymentStatus.PAID
                        : PaymentStatus.FAILED);
        orderRepository.save(order);

        return ipnResponse("00", "Confirm Success");
    }

    @Override
    public boolean verifySignature(Map<String, String> params) {
        String secureHash = params.get("vnp_SecureHash");
        if (!StringUtils.hasText(secureHash)) {
            return false;
        }

        Map<String, String> signedParams = new TreeMap<>();
        params.forEach((key, value) -> {
            if (key != null
                    && key.startsWith("vnp_")
                    && !"vnp_SecureHash".equals(key)
                    && !"vnp_SecureHashType".equals(key)
                    && value != null
                    && !value.isBlank()) {
                signedParams.put(key, value);
            }
        });

        String calculatedHash = hmacSHA512(hashSecret, buildHashData(signedParams));
        return calculatedHash.equalsIgnoreCase(secureHash);
    }

    private Map<String, String> ipnResponse(String code, String message) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("RspCode", code);
        response.put("Message", message);
        return response;
    }

    private String toVnpayAmount(BigDecimal amount) {
        return amount
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .toPlainString();
    }

    private String buildHashData(Map<String, String> params) {
        StringBuilder hashData = new StringBuilder();
        params.forEach((key, value) -> {
            if (value != null && !value.isBlank()) {
                if (!hashData.isEmpty()) {
                    hashData.append('&');
                }
                hashData.append(key)
                        .append('=')
                        .append(urlEncode(value));
            }
        });
        return hashData.toString();
    }

    private String buildQuery(Map<String, String> params) {
        StringBuilder query = new StringBuilder();
        params.forEach((key, value) -> {
            if (value != null && !value.isBlank()) {
                if (!query.isEmpty()) {
                    query.append('&');
                }
                query.append(urlEncode(key))
                        .append('=')
                        .append(urlEncode(value));
            }
        });
        return query.toString();
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot sign VNPAY request", ex);
        }
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwardedFor)) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
