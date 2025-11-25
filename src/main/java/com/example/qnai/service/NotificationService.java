package com.example.qnai.service;

import com.example.qnai.config.TokenProvider;
import com.example.qnai.dto.notification.request.NotificationSettingRequest;
import com.example.qnai.dto.notification.response.NotificationSettingResponse;
import com.example.qnai.entity.UserNotificationSetting;
import com.example.qnai.entity.Users;
import com.example.qnai.global.exception.InvalidTokenException;
import com.example.qnai.global.exception.NotLoggedInException;
import com.example.qnai.global.exception.ResourceNotFoundException;
import com.example.qnai.repository.NotificationRepository;
import com.example.qnai.repository.UserNotificationSettingRepository;
import com.example.qnai.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserNotificationSettingRepository userNotificationSettingRepository;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    //이메일 추출
    private String extractUserEmail(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new NotLoggedInException("로그인이 필요한 요청입니다.");
        }

        String accessToken = bearerToken.substring(7); // "Bearer " 제거

        if (!tokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException("유효하지 않은 Access Token입니다.");
        }

        String email = tokenProvider.extractUsername(accessToken);

        if(email.isEmpty()){
            throw new UsernameNotFoundException("이메일을 추출할 수 없습니다.");
        }

        return email;
    }

    @Transactional
    public NotificationSettingResponse notificationSetting(HttpServletRequest httpServletRequest, NotificationSettingRequest request) {
        String email = extractUserEmail(httpServletRequest);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다."));

        UserNotificationSetting existingNotificationSetting = userNotificationSettingRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("해당 알림 설정이 존재하지 않습니다."));

        //유저의 UserNotificationSetting이 무조건 존재한다는 가정 하에
        if(existingNotificationSetting.isEnabled()){
            existingNotificationSetting.unsubscribe();
        } else{
            existingNotificationSetting.subscribe(request.getPreferredTime());
        }

        userNotificationSettingRepository.save(existingNotificationSetting);

        return NotificationSettingResponse.builder()
                .isEnabled(existingNotificationSetting.isEnabled())
                .preferredTime(existingNotificationSetting.getPreferredTime())
                .build();
    }
}
