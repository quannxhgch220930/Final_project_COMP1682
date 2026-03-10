package com.ecommerce.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Size(max = 300)
    private String slug;

    private String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @Min(value = 0, message = "Tồn kho không được âm")
    private Integer stock = 0;

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;
}