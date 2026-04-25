package com.ecommerce.backend.dto.request;

import lombok.Data;

@Data
public class UpdateProductImageRequest {
    private Boolean isPrimary;
    private Integer sortOrder;
}
