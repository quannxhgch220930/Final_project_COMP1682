package com.ecommerce.backend.mapper;

import com.ecommerce.backend.dto.response.CartItemResponse;
import com.ecommerce.backend.dto.response.CartResponse;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "productId",    source = "product.id")
    @Mapping(target = "productName",  source = "product.name")
    @Mapping(target = "productSlug",  source = "product.slug")
    @Mapping(target = "currentPrice", source = "product.price")
    @Mapping(target = "productImage",
            expression = "java(getFirstImageUrl(item))")
    @Mapping(target = "subtotal",
            expression = "java(item.getPriceSnap().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))")
    CartItemResponse toItemResponse(CartItem item);

    default String getFirstImageUrl(CartItem item) {
        return item.getProduct().getImages().stream()
                .filter(img -> img.isPrimary())
                .findFirst()
                .or(() -> item.getProduct().getImages().stream().findFirst())
                .map(img -> img.getUrl())
                .orElse(null);
    }

    default CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalItems(items.size())
                .totalAmount(total)
                .build();
    }
}