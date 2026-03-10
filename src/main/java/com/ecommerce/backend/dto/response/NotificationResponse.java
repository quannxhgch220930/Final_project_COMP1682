package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.entity.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long             id;
    private NotificationType type;
    private String           title;
    private String           body;
    private boolean          isRead;
    private Long             refId;
    private LocalDateTime    createdAt;
}