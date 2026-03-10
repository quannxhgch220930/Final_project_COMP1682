package com.ecommerce.backend.service;

import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.dto.response.WishlistItemResponse;

import java.util.UUID;

public interface WishlistService {
    PageResponse<WishlistItemResponse> getWishlist(Long userId, int page, int size);
    void addToWishlist(Long userId, Long productId);
    void removeFromWishlist(Long userId, Long productId);
    boolean isInWishlist(Long userId, Long productId);
}