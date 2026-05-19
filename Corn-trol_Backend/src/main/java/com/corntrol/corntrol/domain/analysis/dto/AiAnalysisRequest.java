package com.corntrol.corntrol.domain.analysis.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AiAnalysisRequest {
    private Long recordId;
    private String userId;
    private String content;
}