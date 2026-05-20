package com.corntrol.corntrol.domain.connection.controller;

import com.corntrol.corntrol.domain.connection.dto.AiRecommendResponse;
import com.corntrol.corntrol.domain.connection.dto.ConnectionCreateRequest;
import com.corntrol.corntrol.domain.connection.entity.RecordLink;
import com.corntrol.corntrol.domain.connection.service.ConnectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Connection", description = "알곡 꿰기 (기록 연결 및 추천) API")
@RestController
@RequestMapping("/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @Operation(summary = "연결 추천 요청")
    @PostMapping("/recommend/{recordId}")
    public ResponseEntity<AiRecommendResponse> recommendConnection(
            @PathVariable Long recordId,
            Principal principal) {
        AiRecommendResponse response = connectionService.recommendTarget(principal.getName(), recordId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "연결 추천 결과 조회")
    @GetMapping("/recommend/{recordId}")
    public ResponseEntity<RecordLink> getRecommendResult(
            @PathVariable Long recordId,
            Principal principal) {
        return ResponseEntity.ok(connectionService.getRecommendResult(principal.getName(), recordId));
    }

    @Operation(summary = "연결 생성")
    @PostMapping
    public ResponseEntity<Long> createConnection(
            @RequestBody ConnectionCreateRequest request,
            Principal principal) {
        return ResponseEntity.ok(connectionService.createConnection(principal.getName(), request));
    }

    @Operation(summary = "연결 조회")
    @GetMapping("/{recordId}")
    public ResponseEntity<List<RecordLink>> getConnections(
            @PathVariable Long recordId,
            Principal principal) {
        return ResponseEntity.ok(connectionService.getConnections(principal.getName(), recordId));
    }

    @Operation(summary = "연결 삭제")
    @DeleteMapping("/{linkId}")
    public ResponseEntity<Void> deleteConnection(
            @PathVariable Long linkId,
            Principal principal) {
        connectionService.deleteConnection(principal.getName(), linkId);
        return ResponseEntity.noContent().build();
    }
}