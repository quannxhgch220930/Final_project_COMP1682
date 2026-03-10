package com.ecommerce.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductImageResponse {
    private Long    id;
    private String  url;
    private boolean isPrimary;
    private Integer sortOrder;
}