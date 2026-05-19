package com.corntrol.corntrol.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserStatsResponse {
    private Long totalFocusTime;
    private Long totalRecords;
    private Long totalConnections;
    private Long totalFocusCount;
}