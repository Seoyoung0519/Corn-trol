package com.corntrol.corntrol.domain.connection.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ConnectionCreateRequest {
    private Long sourceRecordId;
    private Long targetRecordId;
}