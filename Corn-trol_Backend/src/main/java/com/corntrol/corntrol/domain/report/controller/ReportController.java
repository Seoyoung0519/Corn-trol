package com.corntrol.corntrol.domain.report.controller;

import com.corntrol.corntrol.domain.report.dto.ReportResponse;
import com.corntrol.corntrol.domain.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Report", description = "리포트 및 통계 조회 API")
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "안티-팝콘 리포트 조회")
    @GetMapping
    public ResponseEntity<ReportResponse.AntiPopcorn> getAntiPopcornReport(Principal principal) {
        return ResponseEntity.ok(reportService.getAntiPopcornReport(principal.getName()));
    }

    @Operation(summary = "몰입 시간 통계 조회")
    @GetMapping("/focus")
    public ResponseEntity<ReportResponse.FocusTime> getFocusTimeReport(Principal principal) {
        return ResponseEntity.ok(reportService.getFocusTimeReport(principal.getName()));
    }

    @Operation(summary = "연결 밀도 조회")
    @GetMapping("/connection")
    public ResponseEntity<ReportResponse.ConnectionDensity> getConnectionDensityReport(Principal principal) {
        return ResponseEntity.ok(reportService.getConnectionDensityReport(principal.getName()));
    }
}