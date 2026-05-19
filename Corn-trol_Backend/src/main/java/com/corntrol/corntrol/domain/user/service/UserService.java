package com.corntrol.corntrol.domain.user.service;

import com.corntrol.corntrol.domain.user.dto.*;
import com.corntrol.corntrol.domain.user.entity.User;
import com.corntrol.corntrol.domain.user.repository.UserRepository;
import com.corntrol.corntrol.domain.report.repository.ReportRepository;
import com.corntrol.corntrol.domain.notification.repository.NotificationRepository;
import com.corntrol.corntrol.domain.focus.repository.FocusSessionRepository;
import com.corntrol.corntrol.domain.focus.repository.CoolingQuestionRepository;
import com.corntrol.corntrol.domain.connection.repository.RecordLinkRepository;
import com.corntrol.corntrol.domain.analysis.repository.AnalysisRepository;
import com.corntrol.corntrol.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ReportRepository reportRepository;
    private final NotificationRepository notificationRepository;
    private final FocusSessionRepository focusSessionRepository;
    private final CoolingQuestionRepository coolingQuestionRepository;
    private final RecordLinkRepository recordLinkRepository;
    private final AnalysisRepository analysisRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signup(SignupRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        userRepository.save(user);

        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.createAccessToken(user.getEmail());
        String refreshToken = jwtUtil.createRefreshToken(user.getEmail());

        user.updateRefreshToken(refreshToken);

        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    public String refreshAccessToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("리프레시 토큰 만료");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰");
        }

        return jwtUtil.createAccessToken(email);
    }

    // 내 정보 조회
    @Transactional(readOnly = true)
    public UserResponse getMyInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage() != null ? user.getProfileImage().name() : null)
                .build();
    }

    // 내 프로필 수정
    @Transactional
    public UserResponse updateMyProfile(UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        user.updateProfile(request.getNickname(), request.getProfileImage());

        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage() != null ? user.getProfileImage().name() : null)
                .build();
    }

    // 내 프로필 조회
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return UserProfileResponse.builder()
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage() != null ? user.getProfileImage().name() : null)
                .build();
    }

    // 내 통계 조회
    @Transactional(readOnly = true)
    public UserStatsResponse getMyStats() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Long userId = user.getId();

        Long totalFocusTime = focusSessionRepository.sumFocusTimeByUserId(userId);
        if (totalFocusTime == null) {
            totalFocusTime = 0L;
        }

        Long totalRecords = (long) user.getRecords().size();
        Long totalConnections = recordLinkRepository.countByUserId(userId);

        Long totalFocusCount = focusSessionRepository.countByUserId(userId);

        return UserStatsResponse.builder()
                .totalFocusTime(totalFocusTime)
                .totalRecords(totalRecords)
                .totalConnections(totalConnections)
                .totalFocusCount(totalFocusCount)
                .build();
    }

    // 로그아웃
    @Transactional
    public void logout() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        user.updateRefreshToken(null);
    }

    // 회원 탈퇴
    @Transactional
    public void withdraw() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Long userId = user.getId();

        reportRepository.deleteByUserId(userId);
        notificationRepository.deleteByUserId(userId);
        focusSessionRepository.deleteByUserId(userId);
        coolingQuestionRepository.deleteByUserId(userId);
        recordLinkRepository.deleteByUserId(userId);
        analysisRepository.deleteByUserId(String.valueOf(userId));

        userRepository.delete(user);
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(UpdatePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }
}