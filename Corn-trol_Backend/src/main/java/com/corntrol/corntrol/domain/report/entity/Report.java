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

    private Double focusTimeTotal;

    private Double shortFormTimeTotal;
    private Double recoveryRate;

    private Integer connectionDensity;

    @Column(columnDefinition = "TEXT")
    private String antiPopcornFeedback;

    @CreationTimestamp
    private LocalDateTime createdAt;
}