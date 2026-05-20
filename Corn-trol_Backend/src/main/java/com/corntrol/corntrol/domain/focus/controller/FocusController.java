package com.corntrol.corntrol.domain.focus.controller;

import com.corntrol.corntrol.domain.focus.dto.QuestionResponse;
import com.corntrol.corntrol.domain.focus.service.FocusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "알곡 식히기", description = "집중 모드 및 AI 질문 생성/조회 API")
@RestController
@RequestMapping("/focus")
@RequiredArgsConstructor
public class FocusController {

    private final FocusService focusService;

    @Data
    public static class ClientRequest {
        private String topic;
        private Long recordId;
    }

    @Operation(summary = "AI 질문 생성 요청")
    @PostMapping("/questions")
    public ResponseEntity<String> requestQuestions(@RequestBody ClientRequest request, Principal principal) {
        focusService.createFocusQuestions(
                principal.getName(),
                request.getRecordId(),
                request.getTopic()
        );
        return ResponseEntity.ok("AI 서버로 질문 생성 요청을 완료했습니다.");
    }

    @Operation(summary = "질문 조회")
    @GetMapping("/questions/{recordId}")
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @PathVariable("recordId") Long recordId,
            Principal principal) {
        List<QuestionResponse> responses = focusService.getQuestions(principal.getName(), recordId);
        return ResponseEntity.ok(responses);
    }

    @Data
    public static class StartRequest {
        private Long recordId;
        private Integer duration;
    }

    @Data
    public static class EndRequest {
        private Long sessionId;
    }

    @Operation(summary = "집중 모드 시작")
    @PostMapping("/start")
    public ResponseEntity<Long> startFocusMode(@RequestBody StartRequest request, Principal principal) {
        Long sessionId = focusService.startFocus(
                principal.getName(),
                request.getRecordId(),
                request.getDuration()
        );
        return ResponseEntity.ok(sessionId);
    }

    @Operation(summary = "집중 모드 종료")
    @PostMapping("/end")
    public ResponseEntity<String> endFocusMode(@RequestBody EndRequest request, Principal principal) {
        focusService.endFocus(principal.getName(), request.getSessionId());
        return ResponseEntity.ok("집중 모드가 종료되었습니다!");
    }
}