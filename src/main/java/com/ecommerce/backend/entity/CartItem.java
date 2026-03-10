package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {

    ;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    // Snapshot giá tại thời điểm thêm vào giỏ
    @Column(name = "price_snap", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceSnap;
}