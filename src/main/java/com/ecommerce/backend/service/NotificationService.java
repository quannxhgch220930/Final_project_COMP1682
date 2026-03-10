package com.ecommerce.backend.service;

import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.dto.response.NotificationResponse;
import com.ecommerce.backend.entity.enums.NotificationType;

public interface NotificationService {
    void push(Long userId, NotificationType type, String title, String body, Long refId);
    PageResponse<NotificationResponse> getMyNotifications(Long userId, int page, int size);
    long countUnread(Long userId);
    void markAllRead(Long userId);
}