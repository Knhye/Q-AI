package com.example.qnai.service;

import com.example.qnai.config.TokenProvider;
import com.example.qnai.dto.user.request.LogoutRequest;
import com.example.qnai.dto.user.request.UserPasswordUpdateRequest;
import com.example.qnai.dto.user.request.UserUpdateRequest;
import com.example.qnai.dto.user.response.UserDetailResponse;
import com.example.qnai.dto.user.response.UserUpdateResponse;
import com.example.qnai.entity.RefreshToken;
import com.example.qnai.entity.Users;
import com.example.qnai.global.exception.InvalidTokenException;
import com.example.qnai.global.exception.NotAcceptableUserException;
import com.example.qnai.global.exception.NotLoggedInException;
import com.example.qnai.global.exception.UserAlreadyLoggedOutException;
import com.example.qnai.repository.RefreshTokenRepository;
import com.example.qnai.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않습니다."));

        return UserDetailResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .mainSubject(user.getMainSubject())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public UserUpdateResponse updateUser(Long id, UserUpdateRequest request) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않습니다."));

        user.updateInfo(
                request.getEmail(),
                request.getNickname(),
                request.getMainSubject()
        );

        return UserUpdateResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .mainSubject(user.getMainSubject())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public void updateUserPassword(Long id, UserPasswordUpdateRequest request) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        String newHashedPassword = passwordEncoder.encode(request.getNewPassword());

        user.updatePassword(newHashedPassword);
    }

    @Transactional
    public void logout(HttpServletRequest httpServletRequest, LogoutRequest request) {
        String accessToken = extractAccessToken(httpServletRequest);

        if (accessToken == null) {
            throw new NotLoggedInException("로그인이 필요한 요청입니다.");
        }

        if (!tokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException("유효하지 않은 Access Token입니다.");
        }

        String username = tokenProvider.extractUsername(accessToken);
        Long expiration = tokenProvider.getExpiration(accessToken);
        if (expiration > 0) {
            String blacklistKey = "blacklist:" + accessToken;
            redisTemplate.opsForValue().set(
                    blacklistKey,
                    "logout",
                    expiration,
                    TimeUnit.MILLISECONDS
            );
        }

        String refreshToken = request.getRefreshToken();
        if (refreshToken != null && !refreshToken.isEmpty()) {
            // Refresh Token 검증
            if (!tokenProvider.validateToken(refreshToken)) {
                throw new InvalidTokenException("유효하지 않은 Refresh Token입니다.");
            }

            // Redis에서 Refresh Token 삭제
            String refreshTokenKey = "refreshToken:" + username;
            redisTemplate.delete(refreshTokenKey);

        }
    }

    private String extractAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거
        }

        return null;
    }

    @Transactional
    public void deleteUser(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않습니다."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String email = userDetails.getUsername();

        if(email.isEmpty()){
            throw new NotLoggedInException("로그인 되지 않은 사용자입니다.");
        }

        if(!Objects.equals(email, user.getEmail())){
            throw new NotAcceptableUserException("접근할 수 없는 유저입니다.");
        }

        user.delete();
        userRepository.save(user);
    }
}
