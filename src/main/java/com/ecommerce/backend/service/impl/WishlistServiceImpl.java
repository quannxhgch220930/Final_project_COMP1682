package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.dto.response.WishlistItemResponse;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.entity.Wishlist;
import com.ecommerce.backend.exception.AppException;
import com.ecommerce.backend.exception.ErrorCode;
import com.ecommerce.backend.mapper.WishlistMapper;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.repository.WishlistRepository;
import com.ecommerce.backend.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository  productRepository;
    private final UserRepository     userRepository;
    private final WishlistMapper     wishlistMapper;

    @Override
    public PageResponse<WishlistItemResponse> getWishlist(Long userId,
                                                          int page, int size) {
        Page<Wishlist> wishPage = wishlistRepository.findAllByUserId(
                userId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));

        return PageResponse.<WishlistItemResponse>builder()
                .content(wishPage.getContent().stream()
                        .map(wishlistMapper::toResponse).toList())
                .page(wishPage.getNumber())
                .size(wishPage.getSize())
                .totalElements(wishPage.getTotalElements())
                .totalPages(wishPage.getTotalPages())
                .last(wishPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public void addToWishlist(Long userId, Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId))
            return;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        wishlistRepository.save(Wishlist.builder()
                .user(user)
                .product(product)
                .build());
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        if (!wishlistRepository.existsByUserIdAndProductId(userId, productId))
            throw new AppException(ErrorCode.WISHLIST_ITEM_NOT_FOUND);

        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }
}