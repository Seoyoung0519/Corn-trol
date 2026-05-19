package com.corntrol.corntrol.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 프론트엔드가 보낸 요청 헤더에서 토큰을 꺼냅니다.
        String token = resolveToken(request);

        // 2. 토큰이 있고, 아까 만든 JwtUtil 공장에서 "이거 진짜 토큰 맞아!" 라고 인정해주면 통과
        if (token != null && jwtUtil.validateToken(token)) {
            // 3. 토큰에서 유저 이메일을 쏙 빼옵니다.
            String email = jwtUtil.getEmailFromToken(token);

            // 4. "이 이메일 가진 유저는 정상적으로 로그인된 사람이다!" 하고 서버(시큐리티 컨텍스트)에 도장을 찍어줍니다.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. 경비원 검사가 끝났으니, 다음 목적지(컨트롤러 등)로 무사히 통과시켜 줍니다.
        filterChain.doFilter(request, response);
    }

    // 💡 프론트엔드가 헤더에 "Bearer 블라블라토큰..." 이렇게 보내기 때문에, 앞에 "Bearer "를 잘라내는 헬퍼 메서드입니다.
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 딱 7글자 자르고 진짜 토큰만 반환!
        }
        return null;
    }
}