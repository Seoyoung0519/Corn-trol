package com.corntrol.corntrol.domain.focus.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cooling_questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class CoolingQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recordId;
    private Long userId;
    private String topic;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
