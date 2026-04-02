package com.ecommerce.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long id;
    private List<CartItemResponse> items;
    private Integer totalItems;  // tổng số loại sản phẩm
    private BigDecimal totalAmount; // tổng tiền
}