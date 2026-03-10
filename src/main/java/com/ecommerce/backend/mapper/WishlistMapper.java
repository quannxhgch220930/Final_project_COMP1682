package com.ecommerce.backend.mapper;

import com.ecommerce.backend.dto.response.WishlistItemResponse;
import com.ecommerce.backend.entity.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(target = "productId",    source = "product.id")
    @Mapping(target = "productName",  source = "product.name")
    @Mapping(target = "productSlug",  source = "product.slug")
    @Mapping(target = "price",        source = "product.price")
    @Mapping(target = "stock",        source = "product.stock")
    @Mapping(target = "isActive",     source = "product.active")
    @Mapping(target = "addedAt",      source = "createdAt")
    @Mapping(target = "productImage",
            expression = "java(getFirstImageUrl(w))")
    WishlistItemResponse toResponse(Wishlist w);

    default String getFirstImageUrl(Wishlist w) {
        return w.getProduct().getImages().stream()
                .filter(img -> img.isPrimary())
                .findFirst()
                .or(() -> w.getProduct().getImages().stream().findFirst())
                .map(img -> img.getUrl())
                .orElse(null);
    }
}