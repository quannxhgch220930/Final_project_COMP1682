package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CouponRequest;
import com.ecommerce.backend.dto.request.CouponValidateRequest;
import com.ecommerce.backend.dto.response.CouponResponse;
import com.ecommerce.backend.entity.Coupon;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CouponService {
    CouponResponse createCoupon(Long adminId, CouponRequest request);
    List<CouponResponse> getAllCoupons();
    CouponResponse updateCoupon(Long id, CouponRequest request);
    void deleteCoupon(Long id);
    Coupon validateAndGet(String code, BigDecimal orderAmount);
    BigDecimal calcDiscount(Coupon coupon, BigDecimal orderAmount);
}