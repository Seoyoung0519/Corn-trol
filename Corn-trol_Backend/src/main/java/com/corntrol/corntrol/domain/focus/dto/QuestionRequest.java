package com.corntrol.corntrol.domain.focus.dto;

import lombok.*;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {
    private Long userId;
    private Long recordId;
    private String topic;
    private RecordDetail currentRecord;
    private List<RecordDetail> linkedRecords;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordDetail {
        private String content;
        private List<String> keywords;
    }
}
