package com.example.qnai.service;

import com.example.qnai.config.TokenProvider;
import com.example.qnai.dto.refreshToken.RefreshDto;
import com.example.qnai.dto.refreshToken.request.RefreshRequest;
import com.example.qnai.dto.refreshToken.response.RefreshResponse;
import com.example.qnai.dto.user.request.LoginRequest;
import com.example.qnai.dto.user.request.SignupRequest;
import com.example.qnai.dto.user.response.LoginResponse;
import com.example.qnai.dto.user.response.SignupResponse;
import com.example.qnai.entity.RefreshToken;
import com.example.qnai.entity.UserNotificationSetting;
import com.example.qnai.entity.Users;
import com.example.qnai.global.exception.InvalidTokenException;
import com.example.qnai.global.exception.UserAlreadyExistException;
import com.example.qnai.repository.RefreshTokenRepository;
import com.example.qnai.repository.UserNotificationSettingRepository;
import com.example.qnai.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserNotificationSettingRepository userNotificationSettingRepository;


    public SignupResponse signup(SignupRequest request){
        if(userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())){
            throw new UserAlreadyExistException("이미 가입된 이메일입니다.");
        }

        Users user = Users.builder()
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .email(request.getEmail())
                .build();

        UserNotificationSetting notificationSetting = UserNotificationSetting.builder()
                .user(user)
                .build();

        user.setNotification(notificationSetting);

        userRepository.save(user);

        userNotificationSettingRepository.save(notificationSetting);

        return SignupResponse.builder()
                .userId(user.getId())
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // ID, PW 검증
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );


        Users user = userRepository.findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));

        // 새 토큰 생성
        String accessToken = tokenProvider.createAccessToken(request.getEmail());

        String refreshToken = tokenProvider.createRefreshToken(request.getEmail());

        // 3. Refresh Token을 Redis/DB에 저장 (화이트리스트)
        long refreshTokenTtl = 7 * 24 * 60 * 60; // 7일 (초 단위)
        refreshTokenRepository.save(refreshToken, user, refreshTokenTtl);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public RefreshResponse refresh(RefreshRequest requestDto){

        RefreshDto existingRefreshToken = refreshTokenRepository.findByToken(requestDto.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("토큰을 찾을 수 없습니다."));

        if(existingRefreshToken.getExpiryDatetime().isBefore(LocalDateTime.now())){
            refreshTokenRepository.deleteByToken(requestDto.getRefreshToken());
            throw new InvalidTokenException("토큰이 만료되었습니다.");
        }

        String username = tokenProvider.extractUsername(existingRefreshToken.getToken());
        String newAccessToken = tokenProvider.createAccessToken(username);

        return RefreshResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }
}
