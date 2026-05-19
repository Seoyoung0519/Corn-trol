package com.corntrol.corntrol.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
    private String nickname;
    private String profileImage;
}
