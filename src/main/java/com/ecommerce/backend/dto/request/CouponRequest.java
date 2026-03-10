package com.ecommerce.backend.dto.request;

import com.ecommerce.backend.entity.enums.CouponType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponRequest {

    @NotBlank(message = "Mã coupon không được để trống")
    @Size(max = 50)
    private String code;

    @NotNull(message = "Loại coupon không được để trống")
    private CouponType type;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal value;

    // null = không giới hạn
    private Integer maxUses;

    @DecimalMin(value = "0.0")
    private BigDecimal minOrder = BigDecimal.ZERO;

    private LocalDateTime expiresAt;
}