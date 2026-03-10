package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderStatusLogResponse {
    private OrderStatus   status;
    private String        note;
    private String        changedBy;
    private LocalDateTime createdAt;
}