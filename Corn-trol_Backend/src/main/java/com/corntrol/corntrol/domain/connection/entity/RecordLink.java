package com.corntrol.corntrol.domain.connection.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "record_links")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class RecordLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(name = "source_record_id")
    private Long sourceRecordId;

    @Column(name = "target_record_id")
    private Long targetRecordId;

    private String topic;

    private Double similarityScore;
    private Double keywordScore;
    private Double finalScore;

    @Builder.Default
    private boolean isConnected = false; // 사용자가 실제로 연결 버튼을 눌렀는지 여부

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }
}
