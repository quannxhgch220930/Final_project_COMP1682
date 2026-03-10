package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.dto.response.NotificationResponse;
import com.ecommerce.backend.entity.Notification;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.entity.enums.NotificationType;
import com.ecommerce.backend.exception.AppException;
import com.ecommerce.backend.exception.ErrorCode;
import com.ecommerce.backend.mapper.NotificationMapper;
import com.ecommerce.backend.repository.NotificationRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository         userRepository;
    private final NotificationMapper     notificationMapper;

    @Override
    @Transactional
    public void push(Long userId, NotificationType type,
                     String title, String body, Long refId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        notificationRepository.save(Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .body(body)
                .refId(refId)
                .build());
    }

    @Override
    public PageResponse<NotificationResponse> getMyNotifications(Long userId,
                                                                 int page, int size) {
        Page<Notification> notifPage = notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(
                        userId, PageRequest.of(page, size));

        return PageResponse.<NotificationResponse>builder()
                .content(notifPage.getContent().stream()
                        .map(notificationMapper::toResponse).toList())
                .page(notifPage.getNumber())
                .size(notifPage.getSize())
                .totalElements(notifPage.getTotalElements())
                .totalPages(notifPage.getTotalPages())
                .last(notifPage.isLast())
                .build();
    }

    @Override
    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }
}