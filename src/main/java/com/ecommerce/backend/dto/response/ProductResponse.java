package com.ecommerce.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Long                     id;
    private String                   name;
    private String                   slug;
    private String                   description;
    private BigDecimal               price;
    private Integer                  stock;
    private BigDecimal               ratingAvg;
    private Integer                  ratingCount;
    private boolean                  isActive;
    private CategoryResponse         category;
    private List<ProductImageResponse> images;
    private LocalDateTime            createdAt;
    private LocalDateTime            updatedAt;
}