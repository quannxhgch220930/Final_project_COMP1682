package com.ecommerce.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private Long       id;
    private String      productId;
    private String     productName;
    private String     productSlug;
    private String     productImage; // URL ảnh chính
    private BigDecimal priceSnap;   // Giá lúc thêm
    private BigDecimal currentPrice;// Giá hiện tại
    private Integer    quantity;
    private BigDecimal subtotal;    // priceSnap × quantity
}