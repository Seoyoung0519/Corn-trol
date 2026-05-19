package com.corntrol.corntrol.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {
    private String nickname;
    private String profileImage;
}