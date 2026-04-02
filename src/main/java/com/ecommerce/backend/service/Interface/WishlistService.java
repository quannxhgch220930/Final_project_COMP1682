package com.ecommerce.backend.service.Interface;

import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.dto.response.WishlistItemResponse;

public interface WishlistService {
    PageResponse<WishlistItemResponse> getWishlist(Long userId, int page, int size);
    void addToWishlist(Long userId, Long productId);
    void removeFromWishlist(Long userId, Long productId);
    boolean isInWishlist(Long userId, Long productId);
}