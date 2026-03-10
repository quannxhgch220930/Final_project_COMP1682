package com.ecommerce.backend.mapper;

import com.ecommerce.backend.dto.response.NotificationResponse;
import com.ecommerce.backend.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toResponse(Notification notification);
}