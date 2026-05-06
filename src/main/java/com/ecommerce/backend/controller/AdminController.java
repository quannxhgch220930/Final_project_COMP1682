package com.ecommerce.backend.controller;

import com.ecommerce.backend.common.ApiResponse;
import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.common.UserHelper;
import com.ecommerce.backend.dto.request.UpdateProductImageRequest;
import com.ecommerce.backend.dto.request.UpdateLockRequest;
import com.ecommerce.backend.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.backend.dto.request.UpdateRoleRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.dto.response.ProductImageResponse;
import com.ecommerce.backend.dto.response.UserResponse;
import com.ecommerce.backend.service.Interface.AdminService;
import com.ecommerce.backend.service.Interface.OrderService;
import com.ecommerce.backend.service.Interface.ProductImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderService orderService;
    private final AdminService adminService;
    private final ProductImageService productImageService;
    private final UserHelper userHelper;

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getAllOrders(status, page, size)));
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Order status updated successfully",
                orderService.updateStatus(userHelper.getUserId(userDetails), id, request)));
    }

    @PostMapping("/products/{id}/images")
    public ResponseEntity<ApiResponse<ProductImageResponse>> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean isPrimary,
            @RequestParam(defaultValue = "0") Integer sortOrder) {
        return ResponseEntity.ok(ApiResponse.success(
                "Product image uploaded successfully",
                productImageService.uploadProductImage(id, file, isPrimary, sortOrder)));
    }

    @PatchMapping("/products/images/{imageId}")
    public ResponseEntity<ApiResponse<ProductImageResponse>> updateProductImage(
            @PathVariable Long imageId,
            @RequestBody UpdateProductImageRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Product image updated successfully",
                productImageService.updateProductImage(
                        imageId,
                        request.getIsPrimary(),
                        request.getSortOrder())));
    }

    @DeleteMapping("/products/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(@PathVariable Long imageId) {
        productImageService.deleteProductImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Product image deleted successfully", null));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                adminService.getAllUsers(page, size)));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getUserById(id)));
    }

    @PatchMapping("/users/{id}/lock")
    public ResponseEntity<ApiResponse<UserResponse>> updateLock(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLockRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                request.getLocked() ? "User account locked" : "User account unlocked",
                adminService.updateLock(id, request)));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "User role updated successfully",
                adminService.updateRole(id, request)));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}