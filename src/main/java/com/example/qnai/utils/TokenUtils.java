package com.example.qnai.utils;

import com.example.qnai.config.TokenProvider;
import com.example.qnai.global.exception.InvalidTokenException;
import com.example.qnai.global.exception.NotLoggedInException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class TokenUtils {
    private final TokenProvider tokenProvider;

    // 생성자 주입
    public TokenUtils(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public String extractUserEmail(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new NotLoggedInException("로그인이 필요한 요청입니다.");
        }

        String accessToken = bearerToken.substring(7); // "Bearer " 제거

        if (!tokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException("유효하지 않은 Access Token입니다.");
        }

        String email = tokenProvider.extractUsername(accessToken);

        if (email.isEmpty()) {
            throw new UsernameNotFoundException("이메일을 추출할 수 없습니다.");
        }

        return email;
    }
}
