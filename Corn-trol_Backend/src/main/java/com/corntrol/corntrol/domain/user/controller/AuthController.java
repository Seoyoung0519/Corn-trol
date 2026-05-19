package com.corntrol.corntrol.domain.user.controller;

import com.corntrol.corntrol.domain.user.dto.*;
import com.corntrol.corntrol.domain.user.service.EmailAuthService;
import com.corntrol.corntrol.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Tag(name = "인증", description = "회원가입, 로그인 및 이메일 인증을 담당합니다.")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final EmailAuthService emailAuthService;

    @Operation(summary = "회원가입", description = "새로운 사용자 정보를 등록합니다. 입력 유효성을 검증합니다.")
    @PostMapping("/signup")
    public UserResponse signup(@Valid @RequestBody SignupRequest request) {
        emailAuthService.checkEmailVerified(request.getEmail());
        UserResponse response = userService.signup(request);
        emailAuthService.removeCompleteAuth(request.getEmail());
        return response;
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인을 시도합니다.")
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenRefreshRequest request) {
        String newAccessToken = userService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(Collections.singletonMap("accessToken", newAccessToken));
    }

    @Operation(summary = "이메일 인증번호 발송", description = "입력한 이메일로 6자리 회원가입 인증번호를 발송합니다.")
    @PostMapping("/email/send")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailSendRequest request) {
        emailAuthService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    @Operation(summary = "이메일 인증번호 검증", description = "발송된 인증번호가 일치하는지 확인합니다. (제한시간 5분)")
    @PostMapping("/email/verify")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody EmailVerifyRequest request) {
        emailAuthService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok("이메일 인증에 성공했습니다.");
    }

    @Operation(summary = "로그아웃", description = "사용자의 로그아웃 요청을 처리하고 리프레시 토큰을 무효화합니다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        userService.logout();
        return ResponseEntity.ok("로그아웃되었습니다.");
    }

    @Operation(summary = "회원 탈퇴", description = "회원 정보를 삭제하고 탈퇴 처리합니다.")
    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw() {
        userService.withdraw();
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    @Operation(summary = "비밀번호 변경", description = "로그인한 사용자의 비밀번호를 변경합니다.")
    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(request);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
}