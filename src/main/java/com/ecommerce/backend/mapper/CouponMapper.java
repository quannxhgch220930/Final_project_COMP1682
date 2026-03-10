package com.ecommerce.backend.mapper;

import com.ecommerce.backend.dto.response.CouponResponse;
import com.ecommerce.backend.entity.Coupon;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    CouponResponse toResponse(Coupon coupon);
}