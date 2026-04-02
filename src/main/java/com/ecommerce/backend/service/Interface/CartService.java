package com.ecommerce.backend.service.Interface;

import com.ecommerce.backend.dto.request.CartItemRequest;
import com.ecommerce.backend.dto.request.UpdateCartItemRequest;
import com.ecommerce.backend.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(Long userId);
    CartResponse addItem(Long userId, CartItemRequest request);
    CartResponse updateItem(Long userId, Long itemId, UpdateCartItemRequest request);
    CartResponse removeItem(Long userId, Long itemId);
    void clearCart(Long userId);
}