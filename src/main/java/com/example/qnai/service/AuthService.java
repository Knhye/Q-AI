package com.example.qnai.service;

import com.example.qnai.config.TokenProvider;
import com.example.qnai.dto.refreshToken.request.RefreshRequest;
import com.example.qnai.dto.refreshToken.response.RefreshResponse;
import com.example.qnai.dto.user.request.LoginRequest;
import com.example.qnai.dto.user.request.SignupRequest;
import com.example.qnai.dto.user.response.LoginResponse;
import com.example.qnai.dto.user.response.SignupResponse;
import com.example.qnai.entity.RefreshToken;
import com.example.qnai.entity.Users;
import com.example.qnai.global.exception.InvalidTokenException;
import com.example.qnai.global.exception.UserAlreadyExistException;
import com.example.qnai.repository.RefreshTokenRepository;
import com.example.qnai.repository.UserRepository;
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


    public SignupResponse signup(SignupRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new UserAlreadyExistException("이미 가입된 이메일입니다.");
        }

        Users user = Users.builder()
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .email(request.getEmail())
                .build();

        userRepository.save(user);

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

        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않습니다."));

//        // 기존 RefreshToken이 존재하면 삭제
//        refreshTokenRepository.findByUser(user)
//                .ifPresent(existing -> {
//                    refreshTokenRepository.delete(existing);
//                    refreshTokenRepository.flush();
//                });

        // 새 토큰 생성
        String accessToken = tokenProvider.createAccessToken(request.getEmail());
        String refreshToken = tokenProvider.createRefreshToken(request.getEmail());

        // 새로운 RefreshToken 저장
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUser(user)
                .map(existing -> existing.updateToken(refreshToken, LocalDateTime.now().plusDays(7))) // update 메서드 사용
                .orElseGet(() -> RefreshToken.builder()
                        .token(refreshToken)
                        .expiryDatetime(LocalDateTime.now().plusDays(7))
                        .user(user)
                        .build());

        refreshTokenRepository.save(refreshTokenEntity);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    public RefreshResponse refresh(RefreshRequest requestDto){
        if(!tokenProvider.validateToken(requestDto.getRefreshToken())){
            throw new InvalidTokenException("토큰 검증에 실패했습니다.");
        }

        RefreshToken existingRefreshToken = refreshTokenRepository.findByToken(requestDto.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("토큰을 찾을 수 없습니다."));

        if(existingRefreshToken.getExpiryDatetime().isBefore(LocalDateTime.now())){
            throw new InvalidTokenException("토큰이 만료되었습니다.");
        }

        String username = tokenProvider.extractUsername(existingRefreshToken.getToken());
        String newAccessToken = tokenProvider.createAccessToken(username);

        return RefreshResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }
}
