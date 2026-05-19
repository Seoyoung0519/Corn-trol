package com.corntrol.corntrol.domain.focus.controller;

import com.corntrol.corntrol.domain.focus.dto.QuestionResponse;
import com.corntrol.corntrol.domain.focus.service.FocusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "알곡 식히기", description = "집중 모드 및 AI 질문 생성/조회 API")
@RestController
@RequestMapping("/focus")
@RequiredArgsConstructor
public class FocusController {

    private final FocusService focusService;

    // 프론트엔드에서 넘어오는 요청 JSON을 받을 내부 클래스
    @Data
    public static class ClientRequest {
        private Long userId;
        private String topic;
        private Long recordId;
    }

    @Operation(summary = "AI 질문 생성 요청", description = "AI 서버와 통신하여 해당 기록에 대한 질문을 생성합니다.")
    @PostMapping("/questions")
    public ResponseEntity<String> requestQuestions(@RequestBody ClientRequest request) {
        focusService.createFocusQuestions(
                request.getUserId(),
                request.getRecordId(),
                request.getTopic()
        );
        return ResponseEntity.ok("AI 서버로 질문 생성 요청을 완료했습니다.");
    }

    @Operation(summary = "질문 조회", description = "해당 기록(recordId)에 생성된 질문 목록을 가져옵니다.")
    @GetMapping("/questions/{recordId}")
    public ResponseEntity<List<QuestionResponse>> getQuestions(@PathVariable("recordId") Long recordId) {
        List<QuestionResponse> responses = focusService.getQuestions(recordId);
        return ResponseEntity.ok(responses);
    }

    // 집중 모드 시작 요청 DTO
    @Data
    public static class StartRequest {
        private Long userId;
        private Long recordId;
        private Integer duration; // 명세서에 추가된 파라미터!
    }

    // 집중 모드 종료 요청 DTO
    @Data
    public static class EndRequest {
        private Long sessionId;
    }

    // 집중 모드 시작
    @Operation(summary = "집중 모드 시작", description = "기록에 대한 집중 모드를 시작하고 세션 ID를 반환합니다.")
    @PostMapping("/start")
    public ResponseEntity<Long> startFocusMode(@RequestBody StartRequest request) {
        Long sessionId = focusService.startFocus(
                request.getUserId(),
                request.getRecordId(),
                request.getDuration()
        );
        return ResponseEntity.ok(sessionId);
    }

    // 집중 모드 종료
    @Operation(summary = "집중 모드 종료", description = "진행 중인 집중 모드 세션을 종료합니다.")
    @PostMapping("/end")
    public ResponseEntity<String> endFocusMode(@RequestBody EndRequest request) {
        focusService.endFocus(request.getSessionId());
        return ResponseEntity.ok("집중 모드가 종료되었습니다!");
    }
}