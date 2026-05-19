package com.corntrol.corntrol.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
