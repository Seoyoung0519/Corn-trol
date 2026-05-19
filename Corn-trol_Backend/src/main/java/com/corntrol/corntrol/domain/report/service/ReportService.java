package com.corntrol.corntrol.domain.report.service;

import com.corntrol.corntrol.domain.report.dto.ReportResponse;
import com.corntrol.corntrol.domain.report.entity.Report;
import com.corntrol.corntrol.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;

    // 공통: 유저의 최신 리포트 가져오기
    private Report getLatestReport(Long userId) {
        return reportRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 리포트가 존재하지 않습니다. userId: " + userId));
    }

    // 안티-팝콘 리포트 조회
    public ReportResponse.AntiPopcorn getAntiPopcornReport(Long userId) {
        Report report = getLatestReport(userId);
        return ReportResponse.AntiPopcorn.builder()
                .antiPopcornFeedback(report.getAntiPopcornFeedback())
                .focusTimeTotal(report.getFocusTimeTotal())
                .shortFormTimeTotal(report.getShortFormTimeTotal())
                .recoveryRate(report.getRecoveryRate())
                .connectionDensity(report.getConnectionDensity())
                .build();
    }

    // 몰입 시간 통계 조회
    public ReportResponse.FocusTime getFocusTimeReport(Long userId) {
        Report report = getLatestReport(userId);
        return ReportResponse.FocusTime.builder()
                .focusTimeTotal(report.getFocusTimeTotal())
                .build();
    }

    // 연결 밀도 조회
    public ReportResponse.ConnectionDensity getConnectionDensityReport(Long userId) {
        Report report = getLatestReport(userId);
        return ReportResponse.ConnectionDensity.builder()
                .connectionDensity(report.getConnectionDensity())
                .build();
    }
}
