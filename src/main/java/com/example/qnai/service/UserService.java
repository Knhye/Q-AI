package com.example.qnai.service;

import com.example.qnai.config.TokenProvider;
import com.example.qnai.dto.user.request.LogoutRequest;
import com.example.qnai.dto.user.request.UserFcmTokenUpdateRequest;
import com.example.qnai.dto.user.request.UserPasswordUpdateRequest;
import com.example.qnai.dto.user.request.UserUpdateRequest;
import com.example.qnai.dto.user.response.UpdateUserPasswordResponse;
import com.example.qnai.dto.user.response.UserDetailResponse;
import com.example.qnai.dto.user.response.UserUpdateResponse;
import com.example.qnai.entity.UserNotificationSetting;
import com.example.qnai.entity.Users;
import com.example.qnai.global.exception.*;
import com.example.qnai.repository.BlacklistRepository;
import com.example.qnai.repository.RefreshTokenRepository;
import com.example.qnai.repository.UserNotificationSettingRepository;
import com.example.qnai.repository.UserRepository;
import com.example.qnai.utils.TokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtils tokenUtils;

    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(HttpServletRequest httpServletRequest) {
        String email = tokenUtils.extractUserEmail(httpServletRequest);

        Users user = userRepository.findByEmailAndIsDeletedFalse(email)
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
    public UserUpdateResponse updateUser(HttpServletRequest httpServletRequest, UserUpdateRequest request) {
        String email = tokenUtils.extractUserEmail(httpServletRequest);

        Users user = userRepository.findByEmailAndIsDeletedFalse(email)
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
    public UpdateUserPasswordResponse updateUserPassword(HttpServletRequest httpServletRequest, UserPasswordUpdateRequest request) {
        String email = tokenUtils.extractUserEmail(httpServletRequest);

        Users user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        String newHashedPassword = passwordEncoder.encode(request.getNewPassword());

        user.updatePassword(newHashedPassword);

        return UpdateUserPasswordResponse.builder()
                .id(user.getId())
                .build();
    }

    @Transactional
    public void updateUserFcmToken(HttpServletRequest httpServletRequest, UserFcmTokenUpdateRequest request) {
        String email = tokenUtils.extractUserEmail(httpServletRequest);

        Users user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다."));

        user.updateFcmToken(request.getFcmToken());
    }


}
