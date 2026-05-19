package com.corntrol.corntrol.domain.focus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionResponse {
    private Long questionId;
    private String content; // 질문 내용
}
