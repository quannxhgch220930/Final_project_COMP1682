package com.ecommerce.backend.dto.request;

import com.ecommerce.backend.entity.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @NotNull(message = "Role không được để trống")
    private Role role;
}