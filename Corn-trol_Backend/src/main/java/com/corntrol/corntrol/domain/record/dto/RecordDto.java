package com.corntrol.corntrol.domain.record.dto;

import lombok.*;
import java.time.LocalDateTime;

public class RecordDto {

    @Getter @Setter @NoArgsConstructor
    public static class CreateRequest {
        private Long userId;
        private String type; // "TEXT" or "VOICE"
        private String content;
        private String audioUrl;
    }

    @Getter @Setter @NoArgsConstructor
    public static class UpdateRequest {
        private String content;
    }

    @Getter @Builder
    public static class Response {
        private Long recordId;
        private Long userId;
        private String type;
        private String content;
        private String audioUrl;
        private String mainTopic;
        private String keywords;
        private LocalDateTime createdAt;

        public static Response from(com.corntrol.corntrol.domain.record.entity.Record record) {
            return Response.builder()
                    .recordId(record.getId())
                    .userId(record.getUser().getId()) // 👈 User 객체 안에서 Id를 꺼내옵니다.
                    .type(record.getType().name())    // 👈 Enum을 String으로 변환합니다.
                    .content(record.getContent())
                    .audioUrl(record.getAudioUrl())
                    .mainTopic(record.getMainTopic())
                    .keywords(record.getKeywords())
                    .createdAt(record.getCreatedAt())
                    .build();
        }
    }
}
