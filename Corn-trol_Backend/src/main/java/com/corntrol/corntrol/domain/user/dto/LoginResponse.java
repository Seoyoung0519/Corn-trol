package com.corntrol.corntrol.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private Long userId;
    private String email;
    private String token;
    private String refreshToken;
}