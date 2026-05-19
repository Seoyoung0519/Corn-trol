package com.corntrol.corntrol.domain.report.dto;

import lombok.Builder;
import lombok.Getter;

public class ReportResponse {

    @Getter
    @Builder
    public static class AntiPopcorn {
        private String antiPopcornFeedback;
        private Double focusTimeTotal;
        private Double shortFormTimeTotal;
        private Double recoveryRate;
        private Integer connectionDensity;
    }

    @Getter
    @Builder
    public static class FocusTime {
        private Double focusTimeTotal;
    }

    @Getter
    @Builder
    public static class ConnectionDensity {
        private Integer connectionDensity;
    }
}
