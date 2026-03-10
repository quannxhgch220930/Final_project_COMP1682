package com.ecommerce.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateLockRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private Boolean locked;
}