package com.example.qnai.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class TokenProvider {
    private final SecretKey key;
    private static final long ACCESS_TOKEN_ABILITY = 1000L * 60 * 60;
    private static final long REFRESH_TOKEN_ABILITY = 1000L * 60 * 60 * 24 * 7;

    public TokenProvider(@Value("${spring.jwt.secret}") String secretKey){
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
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
}
