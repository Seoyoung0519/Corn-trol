package com.corntrol.corntrol.domain.report.controller;

import com.corntrol.corntrol.domain.report.dto.ReportResponse;
import com.corntrol.corntrol.domain.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Report", description = "리포트 및 통계 조회 API")
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "안티-팝콘 리포트 조회", description = "유저의 팝콘 브레인 탈출도, 회복률, 몰입 및 숏폼 소비 데이터를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<ReportResponse.AntiPopcorn> getAntiPopcornReport(
            @Parameter(description = "조회할 유저의 ID") @PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getAntiPopcornReport(userId));
    }

    @Operation(summary = "몰입 시간 통계 조회", description = "유저의 총 몰입 시간 통계 데이터를 조회합니다.")
    @GetMapping("/{userId}/focus")
    public ResponseEntity<ReportResponse.FocusTime> getFocusTimeReport(
            @Parameter(description = "조회할 유저의 ID") @PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getFocusTimeReport(userId));
    }

    @Operation(summary = "연결 밀도 조회", description = "유저의 생각 연결 밀도 수치를 조회합니다.")
    @GetMapping("/{userId}/connection")
    public ResponseEntity<ReportResponse.ConnectionDensity> getConnectionDensityReport(
            @Parameter(description = "조회할 유저의 ID") @PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getConnectionDensityReport(userId));
    }
}
