package com.corntrol.corntrol.domain.connection.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AiRecommendResponse {
    private Integer userId;
    private Long sourceRecordId;
    private Long targetRecordId;
    private String topic;
    private Float similarityScore;
    private Float keywordScore;
    private Float finalScore;
}