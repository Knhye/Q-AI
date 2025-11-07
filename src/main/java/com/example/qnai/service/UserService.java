package com.example.qnai.service;

import com.example.qnai.dto.user.request.LogoutRequest;
import com.example.qnai.dto.user.request.UserUpdateRequest;
import com.example.qnai.dto.user.response.UserDetailResponse;
import com.example.qnai.dto.user.response.UserUpdateResponse;
import com.example.qnai.entity.RefreshToken;
import com.example.qnai.entity.Users;
import com.example.qnai.global.exception.InvalidTokenException;
import com.example.qnai.global.exception.NotLoggedInException;
import com.example.qnai.repository.RefreshTokenRepository;
import com.example.qnai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void logout(LogoutRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {

            throw new NotLoggedInException("로그인된 사용자가 아닙니다.");
        }
        String refreshTokenValue = request.getRefreshToken();

        refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidTokenException("토큰이 존재하지 않습니다."));

        refreshTokenRepository.deleteByToken(refreshTokenValue);

        SecurityContextHolder.clearContext();
    }

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
}
