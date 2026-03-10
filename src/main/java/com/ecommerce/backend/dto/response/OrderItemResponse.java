package com.ecommerce.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class OrderItemResponse {
    private Long      id;
    private Long       productId;
    private String     productName;
    private Integer    quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}