package com.ecommerce.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
public class CategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Size(max = 120)
    private String slug;

    // null = danh mục gốc
    private Long parentId;
}