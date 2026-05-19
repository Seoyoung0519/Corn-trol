package com.corntrol.corntrol.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;

    // 유효 기간 설정 (7일)
    private final long refreshTokenValidityInMs = 604800000L;

    // yml 파일에 적어둔 설정값들을 주입받아서 안전한 SecretKey 객체로 만듭니다.
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-in-ms}") long accessTokenValidityMs,
            @Value("${jwt.refresh-token-validity-in-ms}") long refreshTokenValidityMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityMs = accessTokenValidityMs;
        this.refreshTokenValidityMs = refreshTokenValidityMs;
    }

    // 1. Access Token 생성
    public String createAccessToken(String email) {
        return Jwts.builder()
                .subject(email) // 토큰의 주인 (보통 유저 식별자인 이메일이나 ID를 넣음)
                .issuedAt(new Date(System.currentTimeMillis())) // 발행 시간
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidityMs)) // 만료 시간
                .signWith(secretKey) // 비밀키로 도장 쾅!
                .compact();
    }

    // 2. Refresh Token 생성 (Access Token과 구조는 같지만 수명이 훨씬 깁니다)
    public String createRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenValidityMs))
                .signWith(secretKey)
                .compact();
    }

    // 3. 토큰에서 Email(Subject) 추출하기
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // 4. 토큰이 조작되지 않았는지, 유효기간이 안 지났는지 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            // 실무에서는 ExpiredJwtException, MalformedJwtException 등으로 세분화해서 에러를 던집니다.
            return false;
        }
    }

    // 내부적으로 토큰을 까보는(Parsing) 헬퍼 메서드
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
