package com.corntrol.corntrol.domain.user.service;

import com.corntrol.corntrol.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    // 메모리에 이메일별 [인증번호, 만료시간] 저장
    private final Map<String, VerificationInfo> verificationStorage = new ConcurrentHashMap<>();

    // 인증이 완료된 이메일들을 잠시 보관 (회원가입 기한 10분)
    private final Map<String, LocalDateTime> completeStorage = new ConcurrentHashMap<>();

    // 인증 이메일 생성 및 발송
    public void sendVerificationCode(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 6자리 랜덤 숫자 생성
        String code = String.format("%06d", new Random().nextInt(1000000));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[Corntrol] 회원가입 인증번호 안내");
        message.setText("안녕하세요.\n\n" +
                "회원가입을 위한 인증번호는 [" + code + "] 입니다.\n" +
                "5분 이내에 입력해 주세요.");

        mailSender.send(message);

        // 5분 뒤 만료되도록 설정하여 메모리에 저장
        verificationStorage.put(email, new VerificationInfo(code, LocalDateTime.now().plusMinutes(5)));

    }

    // 입력받은 인증번호 검증
    public void verifyCode(String email, String code) {
        VerificationInfo info = verificationStorage.get(email);

        if (info == null) {
            throw new IllegalArgumentException("인증 요청 내역이 없거나 만료되었습니다.");
        }
        if (info.isExpired()) {
            verificationStorage.remove(email);
            throw new IllegalArgumentException("인증 시간이 초과되었습니다. 다시 요청해 주세요.");
        }
        if (!info.getCode().equals(code)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        // 인증 성공 시 메모리에서 삭제
        verificationStorage.remove(email);
        completeStorage.put(email, LocalDateTime.now().plusMinutes(10));
    }

    // 회원가입 직전 최종 검문 메서드
    public void checkEmailVerified(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        LocalDateTime expiry = completeStorage.get(email);

        // 인증한 적이 없거나, 인증 성공 후 10분이 지나 가입 기한이 만료된 경우
        if (expiry == null || LocalDateTime.now().isAfter(expiry)) {
            completeStorage.remove(email);
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았거나 인증 유효 시간이 만료되었습니다.");
        }
    }

    // 회원가입 완전히 성공 시 최종 삭제
    public void removeCompleteAuth(String email) {
        completeStorage.remove(email);
    }

    private static class VerificationInfo {
        @lombok.Getter private final String code;
        private final LocalDateTime expiredAt;

        public VerificationInfo(String code, LocalDateTime expiredAt) {
            this.code = code;
            this.expiredAt = expiredAt;
        }
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiredAt);
        }
    }
}