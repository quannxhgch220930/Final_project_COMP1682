package com.ecommerce.backend.controller;

import com.ecommerce.backend.common.ApiResponse;
import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.dto.response.WishlistItemResponse;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserRepository  userRepository;

    private Long resolveUserId(UserDetails userDetails) {   // String → Long
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow().getId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<WishlistItemResponse>>> getWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                wishlistService.getWishlist(resolveUserId(userDetails), page, size)));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> add(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {                 // String → Long
        wishlistService.addToWishlist(resolveUserId(userDetails), productId);
        return ResponseEntity.ok(
                ApiResponse.success("Đã thêm vào danh sách yêu thích", null));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> remove(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {                 // String → Long
        wishlistService.removeFromWishlist(resolveUserId(userDetails), productId);
        return ResponseEntity.ok(
                ApiResponse.success("Đã xóa khỏi danh sách yêu thích", null));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> check(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {                 // String → Long
        boolean result = wishlistService.isInWishlist(
                resolveUserId(userDetails), productId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}