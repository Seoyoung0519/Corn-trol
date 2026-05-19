package com.corntrol.corntrol.domain.notification.repository;

import com.corntrol.corntrol.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 유저의 알림을 최신순으로 가져오기
    List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    void deleteByUserId(Long userId);
}
