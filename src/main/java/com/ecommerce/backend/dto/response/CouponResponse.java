package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.entity.enums.CouponType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CouponResponse {
    private Long          id;
    private String        code;
    private CouponType    type;
    private BigDecimal    value;
    private Integer       maxUses;
    private Integer       usedCount;
    private BigDecimal    minOrder;
    private LocalDateTime expiresAt;
    private boolean       isActive;
}