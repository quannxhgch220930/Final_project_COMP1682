package com.ecommerce.backend.controller;

import com.ecommerce.backend.common.ApiResponse;
import com.ecommerce.backend.common.UserHelper;
import com.ecommerce.backend.service.Interface.VnpayPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/payments/vnpay")
@RequiredArgsConstructor
public class VnpayPaymentController {

    private final VnpayPaymentService vnpayPaymentService;
    private final UserHelper userHelper;

    @PostMapping("/create/{orderId}")
    public ResponseEntity<ApiResponse<String>> createPaymentUrl(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId,
            HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "VNPAY payment URL created",
                vnpayPaymentService.createPaymentUrl(
                        userHelper.getUserId(userDetails),
                        orderId,
                        request)));
    }

    @GetMapping("/ipn")
    public ResponseEntity<Map<String, String>> ipn(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(vnpayPaymentService.handleIpn(params));
    }

    @GetMapping("/return")
    public ResponseEntity<ApiResponse<String>> paymentReturn(
            @RequestParam Map<String, String> params) {
        if (!vnpayPaymentService.verifySignature(params)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid VNPAY signature"));
        }
        return ResponseEntity.ok(ApiResponse.success("VNPAY payment result received"));
    }
}
