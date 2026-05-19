package com.corntrol.corntrol.domain.analysis.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AiAnalysisResponse {
    private Long recordId;
    private String userId;
    private String content;
    private String topic;
    private List<String> keywords;
    private List<Double> embedding;
}