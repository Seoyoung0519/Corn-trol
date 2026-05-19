package com.corntrol.corntrol.domain.connection.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AiRecordInfo {
    private Long recordId;
    private String topic;
    private List<String> keywords;
    private List<Double> embedding;
}
