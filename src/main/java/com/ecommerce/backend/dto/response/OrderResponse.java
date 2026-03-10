package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long                      id;
    private OrderStatus               status;
    private BigDecimal                subtotal;
    private BigDecimal                discountAmount;
    private BigDecimal                total;
    private String                    shippingName;
    private String                    shippingPhone;
    private String                    shippingAddress;
    private String                    note;
    private String                    couponCode;
    private List<OrderItemResponse>   items;
    private List<OrderStatusLogResponse> statusLogs;
    private LocalDateTime             createdAt;
    private LocalDateTime             updatedAt;
}