package com.corntrol.corntrol.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(nullable = false)
    private String message;

    @Builder.Default
    private boolean isRead = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 알림 읽음 처리 메서드
    public void markAsRead() {
        this.isRead = true;
    }
}