package com.corntrol.corntrol.domain.notification.dto;

import com.corntrol.corntrol.domain.notification.entity.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}