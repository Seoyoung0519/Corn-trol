package com.corntrol.corntrol.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImage;
}
