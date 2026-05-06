package com.ecommerce.backend;

import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.entity.enums.OrderStatus;
import com.ecommerce.backend.entity.enums.PaymentMethod;
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
import com.ecommerce.backend.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceAccessTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponService couponService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void getMyOrders_shouldQueryByCurrentUserId() {
        Order order = new Order();
        order.setId(10L);

        Page<Order> page = new PageImpl<>(List.of(order));
        OrderResponse orderResponse = OrderResponse.builder().id(10L).build();

        when(orderRepository.findAllByUserId(eq(99L), any(Pageable.class))).thenReturn(page);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        PageResponse<OrderResponse> result = orderService.getMyOrders(99L, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(10L, result.getContent().get(0).getId());
        verify(orderRepository).findAllByUserId(eq(99L), any(Pageable.class));
        verify(orderRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getMyOrderById_shouldRejectOrderOfAnotherUser() {
        when(orderRepository.findByIdAndUserId(5L, 99L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> orderService.getMyOrderById(99L, 5L));

        assertEquals(ErrorCode.ORDER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void cancelOrder_shouldRejectOrderOfAnotherUser() {
        when(orderRepository.findByIdAndUserId(5L, 99L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> orderService.cancelOrder(99L, 5L));

        assertEquals(ErrorCode.ORDER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void cancelOrder_shouldCancelOnlyCurrentUsersOrder() {
        User user = User.builder().email("user@example.com").fullName("User").build();
        user.setId(99L);
        Order order = new Order();
        order.setId(5L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findByIdAndUserId(5L, 99L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(99L, 5L);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertTrue(order.getStatusLogs().stream()
                .anyMatch(log -> log.getStatus() == OrderStatus.CANCELLED));
        verify(orderRepository).save(order);
    }

    @Test
    void checkout_shouldDefaultPaymentMethodToCodWhenMissing() {
        var request = new com.ecommerce.backend.dto.request.OrderRequest();
        request.setReceiverName("User");
        request.setReceiverPhone("0912345678");
        request.setReceiverAddress("Address");

        User user = User.builder().email("user@example.com").fullName("User").build();
        user.setId(99L);
        var cart = new com.ecommerce.backend.entity.Cart();
        var product = com.ecommerce.backend.entity.Product.builder()
                .name("Product")
                .stock(10)
                .isActive(true)
                .build();
        var cartItem = com.ecommerce.backend.entity.CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(1)
                .priceSnap(java.math.BigDecimal.valueOf(100000))
                .build();
        cart.setItems(new java.util.ArrayList<com.ecommerce.backend.entity.CartItem>(List.of(cartItem)));

        var savedOrder = new Order();
        savedOrder.setId(7L);

        when(userRepository.findById(99L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(99L)).thenReturn(Optional.of(cart));
        when(productRepository.save(product)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(orderMapper.toResponse(savedOrder)).thenReturn(OrderResponse.builder().id(7L).build());

        orderService.checkout(99L, request);

        var captor = forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertEquals(PaymentMethod.COD, captor.getValue().getPaymentMethod());
    }
}
