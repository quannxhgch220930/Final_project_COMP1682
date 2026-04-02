package com.ecommerce.backend.controller;

import com.ecommerce.backend.common.ApiResponse;
import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.common.UserHelper;
import com.ecommerce.backend.dto.request.UpdateLockRequest;
import com.ecommerce.backend.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.backend.dto.request.UpdateRoleRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.dto.response.UserResponse;
import com.ecommerce.backend.service.Interface.AdminService;
import com.ecommerce.backend.service.Interface.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderService orderService;
    private final AdminService adminService;
    private final UserHelper   userHelper;

    // ════════════════════════════════════════════
    // ORDER MANAGEMENT
    // ════════════════════════════════════════════

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAllOrders(
            @RequestParam(required = false)    String status,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "10") int    size) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getAllOrders(status, page, size)));
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công",
                orderService.updateStatus(
                        userHelper.getUserId(userDetails), id, request)));
    }

    // ════════════════════════════════════════════
    // USER MANAGEMENT
    // ════════════════════════════════════════════

    // GET /admin/users?page=0&size=10
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                adminService.getAllUsers(page, size)));
    }

    // GET /admin/users/{id}
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                adminService.getUserById(id)));
    }

    // PATCH /admin/users/{id}/lock
    @PatchMapping("/users/{id}/lock")
    public ResponseEntity<ApiResponse<UserResponse>> updateLock(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLockRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                request.getLocked() ? "Đã khóa tài khoản" : "Đã mở khóa tài khoản",
                adminService.updateLock(id, request)));
    }

    // PATCH /admin/users/{id}/role
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Đã cập nhật role",
                adminService.updateRole(id, request)));
    }

    // DELETE /admin/users/{id}
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa người dùng", null));
    }
}