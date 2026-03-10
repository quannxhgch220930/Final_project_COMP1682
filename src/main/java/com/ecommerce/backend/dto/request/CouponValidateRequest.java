package com.ecommerce.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponValidateRequest {

    @NotBlank
    private String code;

    private BigDecimal orderAmount;
}