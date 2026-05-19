package com.corntrol.corntrol.domain.focus.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class FocusSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long recordId;

    private Integer duration; // 설정 시간(분)
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Builder.Default
    private boolean isCompleted = false;

    public void endSession() {
        this.endTime = java.time.LocalDateTime.now();
        this.isCompleted = true;
    }
}
