package com.corntrol.corntrol.domain.notification.service;

import com.corntrol.corntrol.domain.notification.dto.NotificationResponse;
import com.corntrol.corntrol.domain.notification.entity.Notification;
import com.corntrol.corntrol.domain.notification.repository.NotificationRepository;
import com.corntrol.corntrol.domain.user.entity.User;
import com.corntrol.corntrol.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // 알림 목록 조회 (토큰의 이메일 기준 유저 식별)
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. email: " + email));

        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    // 알림 읽음 처리 (토큰 유저와 알림 소유자 일치 여부 검증)
    @Transactional
    public void markAsRead(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. email: " + email));

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다. id: " + id));

        // 소유권 검증: 알림의 userId와 토큰으로 조회한 유저의 id가 다르면 예외 발생
        if (!notification.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("해당 알림에 대한 접근 권한이 없습니다.");
        }

        notification.markAsRead();
    }
}