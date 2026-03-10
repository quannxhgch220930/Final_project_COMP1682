package com.ecommerce.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CategoryResponse {
    private Long             id;
    private String             name;
    private String             slug;
    private Long               parentId;
    private List<CategoryResponse> children;
    private LocalDateTime      createdAt;
}