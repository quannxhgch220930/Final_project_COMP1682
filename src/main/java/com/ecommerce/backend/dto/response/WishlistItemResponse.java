package com.ecommerce.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WishlistItemResponse {
    private Long          id;
    private Long         productId;
    private String        productName;
    private String        productSlug;
    private String        productImage;
    private BigDecimal    price;
    private Integer       stock;
    private boolean       isActive;
    private LocalDateTime addedAt;
}