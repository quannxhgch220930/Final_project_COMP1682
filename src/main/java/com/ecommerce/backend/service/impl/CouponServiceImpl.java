package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.CouponRequest;
import com.ecommerce.backend.dto.response.CouponResponse;
import com.ecommerce.backend.entity.Coupon;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.entity.enums.CouponType;
import com.ecommerce.backend.exception.AppException;
import com.ecommerce.backend.exception.ErrorCode;
import com.ecommerce.backend.mapper.CouponMapper;
import com.ecommerce.backend.repository.CouponRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.Interface.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository   userRepository;
    private final CouponMapper     couponMapper;

    @Override
    @Transactional
    public CouponResponse createCoupon(Long adminId, CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode().toUpperCase()))
            throw new AppException(ErrorCode.COUPON_ALREADY_EXISTS);

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .type(request.getType())
                .value(request.getValue())
                .maxUses(request.getMaxUses())
                .minOrder(request.getMinOrder())
                .expiresAt(request.getExpiresAt())
                .createdBy(admin)
                .build();

        return couponMapper.toResponse(couponRepository.save(coupon));
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(couponMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

        coupon.setType(request.getType());
        coupon.setValue(request.getValue());
        coupon.setMaxUses(request.getMaxUses());
        coupon.setMinOrder(request.getMinOrder());
        coupon.setExpiresAt(request.getExpiresAt());

        return couponMapper.toResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));
        coupon.setActive(false);
        couponRepository.save(coupon);
    }

    @Override
    public Coupon validateAndGet(String code, BigDecimal orderAmount) {
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(code.toUpperCase())
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

        if (coupon.getExpiresAt() != null &&
                coupon.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new AppException(ErrorCode.COUPON_EXPIRED);

        if (coupon.getMaxUses() != null &&
                coupon.getUsedCount() >= coupon.getMaxUses())
            throw new AppException(ErrorCode.COUPON_USAGE_EXCEEDED);

        if (orderAmount.compareTo(coupon.getMinOrder()) < 0)
            throw new AppException(ErrorCode.COUPON_MIN_ORDER_NOT_MET);

        return coupon;
    }

    @Override
    public BigDecimal calcDiscount(Coupon coupon, BigDecimal orderAmount) {
        if (coupon.getType() == CouponType.PERCENT) {
            return orderAmount
                    .multiply(coupon.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return coupon.getValue().min(orderAmount);
    }
}