package com.ecommerce.backend.controller;

import com.ecommerce.backend.common.ApiResponse;
import com.ecommerce.backend.common.UserHelper;
import com.ecommerce.backend.dto.request.CouponRequest;
import com.ecommerce.backend.dto.request.CouponValidateRequest;
import com.ecommerce.backend.dto.response.CouponResponse;
import com.ecommerce.backend.entity.Coupon;
import com.ecommerce.backend.service.Interface.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final UserHelper    userHelper;

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validate(
            @Valid @RequestBody CouponValidateRequest request) {
        Coupon coupon = couponService.validateAndGet(
                request.getCode(), request.getOrderAmount());
        BigDecimal discount = couponService.calcDiscount(coupon, request.getOrderAmount());
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "code",     coupon.getCode(),
                "discount", discount,
                "type",     coupon.getType()
        )));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(couponService.getAllCoupons()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo coupon thành công",
                        couponService.createCoupon(
                                userHelper.getUserId(userDetails), request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> update(
            @PathVariable Long id,          // String → Long
            @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công",
                couponService.updateCoupon(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {        // String → Long
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.success("Vô hiệu hóa coupon thành công", null));
    }
}