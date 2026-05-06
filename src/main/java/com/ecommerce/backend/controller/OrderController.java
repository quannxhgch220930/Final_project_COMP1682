package com.ecommerce.backend.controller;

import com.ecommerce.backend.common.ApiResponse;
import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.common.UserHelper;
import com.ecommerce.backend.dto.request.OrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.dto.response.VnpayCheckoutResponse;
import com.ecommerce.backend.entity.enums.PaymentMethod;
import com.ecommerce.backend.service.Interface.OrderService;
import com.ecommerce.backend.service.Interface.VnpayPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final VnpayPaymentService vnpayPaymentService;
    private final UserHelper   userHelper;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đặt hàng thành công",
                        orderService.checkout(userHelper.getUserId(userDetails), request)));
    }

    @PostMapping("/checkout-vnpay")
    public ResponseEntity<ApiResponse<VnpayCheckoutResponse>> checkoutVnpay(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OrderRequest request,
            HttpServletRequest httpRequest) {
        request.setPaymentMethod(PaymentMethod.VNPAY);
        Long userId = userHelper.getUserId(userDetails);
        OrderResponse order = orderService.checkout(userId, request);
        String paymentUrl = vnpayPaymentService.createPaymentUrl(userId, order.getId(), httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("VNPAY checkout created",
                        VnpayCheckoutResponse.builder()
                                .order(order)
                                .paymentUrl(paymentUrl)
                                .build()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getMyOrders(userHelper.getUserId(userDetails), page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {                        // String → Long
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getMyOrderById(userHelper.getUserId(userDetails), id)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {                        // String → Long
        orderService.cancelOrder(userHelper.getUserId(userDetails), id);
        return ResponseEntity.ok(ApiResponse.success("Hủy đơn hàng thành công", null));
    }
}
