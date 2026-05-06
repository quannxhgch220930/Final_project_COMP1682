package com.ecommerce.backend.service.Interface;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface VnpayPaymentService {
    String createPaymentUrl(Long userId, Long orderId, HttpServletRequest request);

    Map<String, String> handleIpn(Map<String, String> params);

    boolean verifySignature(Map<String, String> params);
}
