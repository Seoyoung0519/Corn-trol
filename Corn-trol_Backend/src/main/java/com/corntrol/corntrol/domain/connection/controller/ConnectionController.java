package com.corntrol.corntrol.domain.connection.controller;

import com.corntrol.corntrol.domain.connection.dto.AiRecommendResponse;
import com.corntrol.corntrol.domain.connection.dto.ConnectionCreateRequest;
import com.corntrol.corntrol.domain.connection.dto.ConnectionRecommendRequest;
import com.corntrol.corntrol.domain.connection.entity.RecordLink;
import com.corntrol.corntrol.domain.connection.service.ConnectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Connection", description = "알곡 꿰기 (기록 연결 및 추천) API")
@RestController
@RequestMapping("/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @Operation(summary = "연결 추천 요청", description = "선택한 기록과 같은 주제를 가진 다른 기록들 중 가장 연관성이 높은 기록을 AI를 통해 추천받습니다.")
    @PostMapping("/recommend")
    public ResponseEntity<AiRecommendResponse> recommendConnection(@RequestBody ConnectionRecommendRequest request) {
        AiRecommendResponse response = connectionService.recommendTarget(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "연결 추천 결과 조회", description = "특정 기록에 대해 AI가 추천했던(대기 중인) 결과를 조회합니다.")
    @GetMapping("/recommend/{recordId}")
    public ResponseEntity<RecordLink> getRecommendResult(@PathVariable Long recordId) {
        return ResponseEntity.ok(connectionService.getRecommendResult(recordId));
    }

    @Operation(summary = "연결 생성", description = "추천받은 기록과의 연결을 최종적으로 수락(생성)합니다.")
    @PostMapping
    public ResponseEntity<Long> createConnection(@RequestBody ConnectionCreateRequest request) {
        return ResponseEntity.ok(connectionService.createConnection(request));
    }

    @Operation(summary = "연결 조회", description = "특정 기록과 연결이 완료된 모든 기록 목록을 조회합니다.")
    @GetMapping("/{recordId}")
    public ResponseEntity<List<RecordLink>> getConnections(@PathVariable Long recordId) {
        return ResponseEntity.ok(connectionService.getConnections(recordId));
    }

    @Operation(summary = "연결 삭제", description = "기존에 연결된 기록(RecordLink)을 삭제(연결 끊기)합니다.")
    @DeleteMapping("/{linkId}")
    public ResponseEntity<Void> deleteConnection(@PathVariable Long linkId) {
        connectionService.deleteConnection(linkId);
        return ResponseEntity.noContent().build();
    }
}