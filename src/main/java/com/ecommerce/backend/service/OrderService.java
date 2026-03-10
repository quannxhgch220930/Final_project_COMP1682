package com.ecommerce.backend.service;

import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.dto.request.OrderRequest;
import com.ecommerce.backend.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.backend.dto.response.OrderResponse;

import java.util.UUID;

public interface OrderService {
    OrderResponse checkout(Long userId, OrderRequest request);
    PageResponse<OrderResponse> getMyOrders(Long userId, int page, int size);
    OrderResponse getMyOrderById(Long userId, Long orderId);
    void cancelOrder(Long userId, Long orderId);
    PageResponse<OrderResponse> getAllOrders(String status, int page, int size);
    OrderResponse updateStatus(Long adminId, Long orderId, UpdateOrderStatusRequest request);
}