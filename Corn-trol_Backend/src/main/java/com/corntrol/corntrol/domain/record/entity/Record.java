package com.corntrol.corntrol.domain.record.entity;

import com.corntrol.corntrol.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private RecordType type; // TEXT, VOICE

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String audioUrl;

    private String mainTopic;

    @Column(columnDefinition = "TEXT")
    private String keywords;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateAnalysisResults(String mainTopic, String keywords) {
        this.mainTopic = mainTopic;
        this.keywords = keywords;
    }
}
