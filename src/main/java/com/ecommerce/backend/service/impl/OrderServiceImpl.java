package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.dto.request.OrderRequest;
import com.ecommerce.backend.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.CartItem;
import com.ecommerce.backend.entity.Coupon;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.OrderItem;
import com.ecommerce.backend.entity.OrderStatusLog;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.entity.enums.NotificationType;
import com.ecommerce.backend.entity.enums.OrderStatus;
import com.ecommerce.backend.exception.AppException;
import com.ecommerce.backend.exception.ErrorCode;
import com.ecommerce.backend.mapper.OrderMapper;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.CouponRepository;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.Interface.CouponService;
import com.ecommerce.backend.service.Interface.NotificationService;
import com.ecommerce.backend.service.Interface.OrderService;
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

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final CouponService couponService;
    private final NotificationService notificationService;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse checkout(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_EMPTY));

        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (!product.isActive()) {
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            subtotal = subtotal.add(
                    cartItem.getPriceSnap()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        Coupon coupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            coupon = couponService.validateAndGet(request.getCouponCode(), subtotal);
            discountAmount = couponService.calcDiscount(coupon, subtotal);
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }

        BigDecimal total = subtotal.subtract(discountAmount);

        Order order = Order.builder()
                .user(user)
                .coupon(coupon)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .total(total)
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .receiverAddress(request.getReceiverAddress())
                .note(request.getNote())
                .build();

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

        OrderStatusLog log = OrderStatusLog.builder()
                .order(order)
                .status(OrderStatus.PENDING)
                .note("Order has been created")
                .build();
        order.getStatusLogs().add(log);

        Order saved = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        notificationService.push(
                userId,
                NotificationType.ORDER,
                "Order placed successfully",
                "Order #" + saved.getId() + " has been placed. Total: " + total + " VND",
                saved.getId());

        if (coupon != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            notificationService.push(
                    userId,
                    NotificationType.COUPON,
                    "Coupon applied successfully",
                    "Coupon " + coupon.getCode() + " was applied. You saved " + discountAmount + " VND.",
                    saved.getId());
        }

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

        if (order.getStatus() != OrderStatus.PENDING
                && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new AppException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        order.setStatus(OrderStatus.CANCELLED);

        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        });

        if (order.getCoupon() != null) {
            Coupon coupon = order.getCoupon();
            coupon.setUsedCount(Math.max(0, coupon.getUsedCount() - 1));
            couponRepository.save(coupon);
        }

        OrderStatusLog log = OrderStatusLog.builder()
                .order(order)
                .status(OrderStatus.CANCELLED)
                .note("Order was canceled by the user")
                .build();
        order.getStatusLogs().add(log);
        orderRepository.save(order);

        notificationService.push(
                userId,
                NotificationType.ORDER,
                "Order canceled",
                "Order #" + orderId + " was canceled successfully.",
                orderId);
    }

    @Override
    public PageResponse<OrderResponse> getAllOrders(String status, int page, int size) {
        Page<Order> orderPage;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (status != null && !status.isBlank()) {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orderPage = orderRepository.findAllByStatus(orderStatus, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        return toPageResponse(orderPage);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long adminId, Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        OrderStatus nextStatus = request.getStatus();
        validateStatusTransition(order.getStatus(), nextStatus);

        order.setStatus(nextStatus);

        OrderStatusLog log = OrderStatusLog.builder()
                .order(order)
                .status(nextStatus)
                .changedBy(admin)
                .note(request.getNote())
                .build();
        order.getStatusLogs().add(log);
        orderRepository.save(order);

        notificationService.push(
                order.getUser().getId(),
                NotificationType.ORDER,
                "Order status updated",
                "Order #" + orderId + " is now " + nextStatus.name(),
                orderId);

        return orderMapper.toResponse(order);
    }

    private PageResponse<OrderResponse> toPageResponse(Page<Order> page) {
        return PageResponse.<OrderResponse>builder()
                .content(page.getContent().stream()
                        .map(orderMapper::toResponse)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus nextStatus) {
        if (currentStatus == nextStatus) {
            return;
        }

        boolean valid = switch (currentStatus) {
            case PENDING -> nextStatus == OrderStatus.CONFIRMED
                    || nextStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> nextStatus == OrderStatus.PROCESSING
                    || nextStatus == OrderStatus.CANCELLED;
            case PROCESSING -> nextStatus == OrderStatus.SHIPPING;
            case SHIPPING -> nextStatus == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!valid) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }
    }
}