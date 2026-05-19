package com.corntrol.corntrol.domain.user.controller;

import com.corntrol.corntrol.domain.user.dto.UpdateProfileRequest;
import com.corntrol.corntrol.domain.user.dto.UserProfileResponse;
import com.corntrol.corntrol.domain.user.dto.UserResponse;
import com.corntrol.corntrol.domain.user.dto.UserStatsResponse;
import com.corntrol.corntrol.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 API", description = "사용자 정보 조회 및 프로필 수정을 담당합니다.")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 상세 정보를 가져옵니다.")
    @GetMapping("/me")
    public UserResponse getMyInfo() {
        return userService.getMyInfo();
    }

    @Operation(summary = "내 프로필 수정", description = "현재 로그인한 사용자의 닉네임이나 프로필 정보를 업데이트합니다.")
    @PutMapping("/me")
    public UserResponse updateMyProfile(@RequestBody UpdateProfileRequest request) {
        return userService.updateMyProfile(request);
    }

    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 닉네임과 프로필 이미지를 조회합니다.")
    @GetMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @Operation(summary = "내 통계 조회", description = "현재 로그인한 사용자의 전체 몰입 시간, 기록 개수, 연결 노드 개수 통계를 조회합니다.")
    @GetMapping("/me/stats")
    public ResponseEntity<UserStatsResponse> getMyStats() {
        return ResponseEntity.ok(userService.getMyStats());
    }
}
