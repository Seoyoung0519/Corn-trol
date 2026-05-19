package com.corntrol.corntrol.domain.analysis.controller;

import com.corntrol.corntrol.domain.analysis.dto.AiAnalysisResponse;
import com.corntrol.corntrol.domain.analysis.dto.AnalysisCreateRequest;
import com.corntrol.corntrol.domain.analysis.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Analysis", description = "기록 분석(AI) API")
@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @Operation(summary = "기록 분석 결과 조회", description = "특정 기록(recordId)에 대해 이미 완료된 분석 결과를 조회합니다.")
    @GetMapping("/{recordId}")
    public ResponseEntity<AiAnalysisResponse> getAnalysis(@PathVariable Long recordId) {
        AiAnalysisResponse response = analysisService.getAnalysis(recordId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "기록 분석 요청", description = "DB의 기록을 AI 서버로 전송하여 주제, 키워드, 임베딩을 추출하고 저장합니다.")
    @PostMapping
    public ResponseEntity<AiAnalysisResponse> requestAnalysis(@RequestBody AnalysisCreateRequest request) {
        AiAnalysisResponse response = analysisService.analyzeAndSave(request);
        return ResponseEntity.ok(response);
    }
}