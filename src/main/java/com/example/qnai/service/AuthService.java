package com.example.qnai.service;

import com.example.qnai.config.TokenProvider;
import com.example.qnai.dto.user.request.LoginRequest;
import com.example.qnai.dto.user.request.LogoutRequest;
import com.example.qnai.dto.user.request.SignupRequest;
import com.example.qnai.dto.user.response.LoginResponse;
import com.example.qnai.dto.user.response.SignupResponse;
import com.example.qnai.entity.RefreshToken;
import com.example.qnai.entity.Users;
import com.example.qnai.global.exception.InvalidTokenException;
import com.example.qnai.global.exception.NotLoggedInException;
import com.example.qnai.global.exception.UserAlreadyExistException;
import com.example.qnai.repository.RefreshTokenRepository;
import com.example.qnai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        Optional<Users> optionalUser = userRepository.findByEmail(request.getEmail());

        if(optionalUser.isPresent()){
            throw new UserAlreadyExistException("이미 존재하는 이메일입니다.");
        }

        Users savedUser = Users.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .mainSubject(null)
                .notebooks(null)
                .build();

        userRepository.save(savedUser);

        return SignupResponse.builder()
                .userId(savedUser.getId())
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Users user = userRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않습니다."));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String refreshToken = tokenProvider.createRefreshToken(request.getEmail());
        String accessToken = tokenProvider.createAccessToken(request.getEmail());

        //이미 refresh 토큰이 존재하는지 확인
        refreshTokenRepository.findByUserEmail(request.getEmail()).ifPresentOrElse(
                // 2-1. 기존 토큰이 존재하면 -> UPDATE
                existingToken -> {
                    // 토큰 문자열과 만료 시간을 새로 갱신합니다.
                    existingToken.updateToken(refreshToken, LocalDateTime.now().plusDays(7));
                },
                // 2-2. 기존 토큰이 없으면 -> INSERT
                () -> {
                    RefreshToken newRefreshToken = RefreshToken.builder()
                            .token(refreshToken)
                            .expiryDatetime(LocalDateTime.now().plusDays(7))
                            .user(user)
                            .build();
                    refreshTokenRepository.save(newRefreshToken);
                }
        );

        return LoginResponse.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
    }


}
