package com.ecommerce.backend.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateCartItemRequest {

    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
    private Integer quantity;
}