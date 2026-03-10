package com.ecommerce.backend.mapper;

import com.ecommerce.backend.dto.response.OrderItemResponse;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.dto.response.OrderStatusLogResponse;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.OrderItem;
import com.ecommerce.backend.entity.OrderStatusLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "couponCode",
            expression = "java(order.getCoupon() != null ? order.getCoupon().getCode() : null)")
    @Mapping(target = "items",      source = "items")
    @Mapping(target = "statusLogs", source = "statusLogs")
    OrderResponse toResponse(Order order);

    @Mapping(target = "productId", source = "product.id")
    OrderItemResponse toItemResponse(OrderItem item);

    @Mapping(target = "changedBy",
            expression = "java(log.getChangedBy() != null ? log.getChangedBy().getFullName() : \"System\")")
    OrderStatusLogResponse toLogResponse(OrderStatusLog log);
}