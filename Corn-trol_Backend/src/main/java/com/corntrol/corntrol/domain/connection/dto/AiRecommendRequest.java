package com.corntrol.corntrol.domain.connection.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AiRecommendRequest {
    private Integer userId;
    private AiRecordInfo sourceRecord;
    private List<AiRecordInfo> candidateRecords;
}
