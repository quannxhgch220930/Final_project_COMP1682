package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.entity.enums.OrderStatus;
import com.ecommerce.backend.entity.enums.PaymentMethod;
import com.ecommerce.backend.entity.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long                      id;
    private Long                      userId;
    private String                    userEmail;
    private String                    userFullName;
    private OrderStatus               status;
    private BigDecimal                subtotal;
    private BigDecimal                discountAmount;
    private BigDecimal                total;
    private PaymentStatus             paymentStatus;
    private PaymentMethod             paymentMethod;
    private String                    vnpTxnRef;
    private String                    vnpTransactionNo;
    private String                    receiverName;
    private String                    receiverPhone;
    private String                    receiverAddress;
    private String                    note;
    private String                    couponCode;
    private List<OrderItemResponse>   items;
    private List<OrderStatusLogResponse> statusLogs;
    private LocalDateTime             createdAt;
    private LocalDateTime             updatedAt;
}
