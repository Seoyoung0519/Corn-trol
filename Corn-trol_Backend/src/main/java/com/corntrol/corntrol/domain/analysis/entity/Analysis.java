package com.corntrol.corntrol.domain.analysis.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Analysis {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recordId;

    private String userId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String topic;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "analysis_keywords", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    @Convert(converter = DoubleListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Double> embedding;

    @Builder
    public Analysis(Long recordId, String userId, String content, String topic, List<String> keywords, List<Double> embedding) {
        this.recordId = recordId;
        this.userId = userId;
        this.content = content;
        this.topic = topic;
        this.keywords = keywords;
        this.embedding = embedding;
    }
}
