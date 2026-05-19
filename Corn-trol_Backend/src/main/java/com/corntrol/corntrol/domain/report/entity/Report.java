package com.corntrol.corntrol.domain.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Double focusTimeTotal; // 총 몰입 시간

    private Double shortFormTimeTotal; // 숏폼 소비 시간
    private Double recoveryRate; // 알곡 회복률 (%)

    private Integer connectionDensity; // 연결 밀도

    @Column(columnDefinition = "TEXT")
    private String antiPopcornFeedback; // AI 리포트 내용

    @CreationTimestamp
    private LocalDateTime createdAt;
}