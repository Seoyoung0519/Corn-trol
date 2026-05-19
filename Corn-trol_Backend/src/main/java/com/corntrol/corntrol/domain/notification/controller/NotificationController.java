package com.corntrol.corntrol.domain.notification.controller;

import com.corntrol.corntrol.domain.notification.dto.NotificationResponse;
import com.corntrol.corntrol.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "알림", description = "알림 조회 및 읽음 처리 API")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회", description = "현재 로그인한 유저(JWT 토큰 기준)의 모든 알림을 최신순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @Parameter(hidden = true) Authentication authentication) {
        // Spring Security 인증 객체에서 유저 이메일(sub) 추출
        String email = authentication.getName();
        return ResponseEntity.ok(notificationService.getNotifications(email));
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림의 상태를 읽음으로 변경합니다. 본인의 알림만 처리 가능합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "읽음 처리할 알림의 ID") @PathVariable("id") Long id,
            @Parameter(hidden = true) Authentication authentication) {
        // Spring Security 인증 객체에서 유저 이메일(sub) 추출
        String email = authentication.getName();
        notificationService.markAsRead(id, email);
        return ResponseEntity.ok().build();
    }
}