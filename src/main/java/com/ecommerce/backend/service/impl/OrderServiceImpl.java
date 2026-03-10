package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.dto.request.OrderRequest;
import com.ecommerce.backend.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.entity.*;
import com.ecommerce.backend.entity.enums.NotificationType;
import com.ecommerce.backend.entity.enums.OrderStatus;
import com.ecommerce.backend.exception.AppException;
import com.ecommerce.backend.exception.ErrorCode;
import com.ecommerce.backend.mapper.OrderMapper;
import com.ecommerce.backend.repository.*;
import com.ecommerce.backend.service.CouponService;
import com.ecommerce.backend.service.NotificationService;
import com.ecommerce.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository     orderRepository;
    private final UserRepository      userRepository;
    private final CartRepository      cartRepository;
    private final ProductRepository   productRepository;
    private final CouponRepository    couponRepository;
    private final CouponService       couponService;
    private final NotificationService notificationService;
    private final OrderMapper         orderMapper;

    @Override
    @Transactional
    public OrderResponse checkout(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_EMPTY));

        if (cart.getItems().isEmpty())
            throw new AppException(ErrorCode.CART_EMPTY);

        // Tính subtotal + trừ tồn kho
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (!product.isActive())
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            if (product.getStock() < cartItem.getQuantity())
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);

            subtotal = subtotal.add(
                    cartItem.getPriceSnap()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        // Xử lý coupon
        BigDecimal discountAmount = BigDecimal.ZERO;
        Coupon coupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            coupon = couponService.validateAndGet(request.getCouponCode(), subtotal);
            discountAmount = couponService.calcDiscount(coupon, subtotal);
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }

        BigDecimal total = subtotal.subtract(discountAmount);

        // Tạo Order
        Order order = Order.builder()
                .user(user)
                .coupon(coupon)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .total(total)
                .shippingName(request.getReceiverName())
                .shippingPhone(request.getReceiverPhone())
                .shippingAddress(request.getReceiverAddress())
                .note(request.getNote())
                .build();

        // Tạo OrderItems
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(ci -> OrderItem.builder()
                        .order(order)
                        .product(ci.getProduct())
                        .productName(ci.getProduct().getName())
                        .quantity(ci.getQuantity())
                        .unitPrice(ci.getPriceSnap())
                        .subtotal(ci.getPriceSnap()
                                .multiply(BigDecimal.valueOf(ci.getQuantity())))
                        .build())
                .toList();

        order.setItems(orderItems);

        // Log trạng thái đầu tiên
        OrderStatusLog log = OrderStatusLog.builder()
                .order(order)
                .status(OrderStatus.PENDING)
                .note("Đơn hàng vừa được tạo")
                .build();
        order.getStatusLogs().add(log);

        Order saved = orderRepository.save(order);

        // Xóa giỏ hàng
        cart.getItems().clear();
        cartRepository.save(cart);

        // Thông báo
        notificationService.push(
                userId, NotificationType.ORDER,
                "Đặt hàng thành công! 🎉",
                "Đơn hàng #" + saved.getId()
                        + " đã được đặt. Tổng tiền: " + total + "đ",
                saved.getId());

        return orderMapper.toResponse(saved);
    }

    @Override
    public PageResponse<OrderResponse> getMyOrders(Long userId, int page, int size) {
        Page<Order> orderPage = orderRepository.findAllByUserId(
                userId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return toPageResponse(orderPage);
    }

    @Override
    public OrderResponse getMyOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.PENDING &&
                order.getStatus() != OrderStatus.CONFIRMED)
            throw new AppException(ErrorCode.ORDER_CANNOT_CANCEL);

        order.setStatus(OrderStatus.CANCELLED);

        // Hoàn lại tồn kho
        order.getItems().forEach(item -> {
            Product p = item.getProduct();
            p.setStock(p.getStock() + item.getQuantity());
            productRepository.save(p);
        });

        // Hoàn lại coupon
        if (order.getCoupon() != null) {
            Coupon c = order.getCoupon();
            c.setUsedCount(Math.max(0, c.getUsedCount() - 1));
            couponRepository.save(c);
        }

        OrderStatusLog log = OrderStatusLog.builder()
                .order(order)
                .status(OrderStatus.CANCELLED)
                .note("Người dùng hủy đơn hàng")
                .build();
        order.getStatusLogs().add(log);
        orderRepository.save(order);

        notificationService.push(
                userId, NotificationType.ORDER,
                "Đơn hàng đã bị hủy",
                "Đơn hàng #" + orderId + " đã được hủy thành công.",
                orderId);
    }

    @Override
    public PageResponse<OrderResponse> getAllOrders(String status, int page, int size) {
        Page<Order> orderPage;
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        if (status != null && !status.isBlank()) {
            OrderStatus os = OrderStatus.valueOf(status.toUpperCase());
            orderPage = orderRepository.findAllByStatus(os, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        return toPageResponse(orderPage);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long adminId, Long orderId,
                                      UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        order.setStatus(request.getStatus());

        OrderStatusLog log = OrderStatusLog.builder()
                .order(order)
                .status(request.getStatus())
                .changedBy(admin)
                .note(request.getNote())
                .build();
        order.getStatusLogs().add(log);
        orderRepository.save(order);

        notificationService.push(
                order.getUser().getId(), NotificationType.ORDER,
                "Cập nhật đơn hàng",
                "Đơn hàng #" + orderId
                        + " đã chuyển sang: " + request.getStatus().name(),
                orderId);

        return orderMapper.toResponse(order);
    }

    // ── Helper ─────────────────────────────────────────────

    private PageResponse<OrderResponse> toPageResponse(Page<Order> page) {
        return PageResponse.<OrderResponse>builder()
                .content(page.getContent().stream()
                        .map(orderMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}