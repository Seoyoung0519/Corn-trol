package com.corntrol.corntrol.domain.focus.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class AiApiDto {

    @Getter @Builder
    public static class Request {
        private Long userId;
        private Long recordId;
        private String topic;
        private RecordInfo currentRecord;
        private List<LinkedRecordInfo> linkedRecords;
    }

    @Getter @Builder
    public static class RecordInfo {
        private String content;
        private List<String> keywords;
    }

    @Getter @Builder
    public static class LinkedRecordInfo {
        private Long recordId;
        private String content;
        private List<String> keywords;
    }

    @Getter @NoArgsConstructor
    public static class Response {
        private List<QuestionInfo> questions;
    }

    @Getter @NoArgsConstructor
    public static class QuestionInfo {
        private Long id;
        private Long recordId;
        private Long userId;
        private String topic;
        private String questionText;
        private LocalDateTime createdAt;
    }
}