package com.example.qnai.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Component
public class TokenProvider {
    private final SecretKey key;
    private static final long ACCESS_TOKEN_ABILITY = 1000L * 60 * 60;
    private static final long REFRESH_TOKEN_ABILITY = 1000L * 60 * 60 * 24 * 7;

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    public TokenProvider(@Value("${spring.jwt.secret}") String secretKey, RedisTemplate<String, Object> redisTemplate){
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.redisTemplate = redisTemplate;
    }


    //액세스 토큰 생성
    public String createAccessToken(String username){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_TOKEN_ABILITY);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    //리프레시 토큰 생성
    public String createRefreshToken(String username){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + REFRESH_TOKEN_ABILITY);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    //사용자 정보 추출
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(this.key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    //토큰 유효성 검사
    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(this.key) // SecretKey
                    .build()
                    .parseSignedClaims(token); // 서명된 클레임 파싱 및 검증
            return true;
        } catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public boolean deleteRefreshToken(String refreshToken) {
        try {
            String userId = extractUsername(refreshToken);

            // Redis에서 해당 userId에 연결된 Refresh Token 키 삭제
            // Redis에 저장할 때 사용했던 키 패턴("refreshToken:{userId}")을 동일하게 사용
            Boolean deleted = redisTemplate.delete("refreshToken:" + userId);

            return Boolean.TRUE.equals(deleted); // null 체크를 위해 Boolean.TRUE.equals() 사용
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("유효하지 않은 Refresh Token: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Refresh Token 삭제 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    public Long getExpiration(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            Date now = new Date();

            long remainingTime = expiration.getTime() - now.getTime();
            return remainingTime > 0 ? remainingTime : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    public boolean isBlacklisted(String accessToken) {
        String blacklistKey = "blacklist:" + accessToken;
        try {
            Boolean hasKey = redisTemplate.hasKey(blacklistKey);
            return Boolean.TRUE.equals(hasKey);
        } catch (Exception e) {
            System.out.println("블랙리스트 예외 발생");
            return false;
        }
    }
}
