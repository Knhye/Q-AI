package com.example.qnai.service;

import com.example.qnai.dto.notification.request.NotificationReadRequest;
import com.example.qnai.dto.notification.request.NotificationSettingRequest;
import com.example.qnai.dto.notification.response.NotificationItem;
import com.example.qnai.dto.notification.response.NotificationResponse;
import com.example.qnai.dto.notification.response.NotificationSettingResponse;
import com.example.qnai.entity.Notification;
import com.example.qnai.entity.UserNotificationSetting;
import com.example.qnai.entity.Users;
import com.example.qnai.global.exception.ResourceNotFoundException;
import com.example.qnai.repository.NotificationRepository;
import com.example.qnai.repository.UserNotificationSettingRepository;
import com.example.qnai.repository.UserRepository;
import com.example.qnai.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserNotificationSettingRepository userNotificationSettingRepository;
    private final UserRepository userRepository;
    private final ExternalPushService externalPushService;
    private final TokenUtils tokenUtils;

    //구독 설정/해지
    @Transactional
    public NotificationSettingResponse notificationSetting(HttpServletRequest httpServletRequest, NotificationSettingRequest request) {
        String email = tokenUtils.extractUserEmail(httpServletRequest);

        Users user = userRepository.findByEmailAndIsDeletedFalse(email)
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

    //주기적으로 Scheduler에 의해 호출되는 메서드
    @Transactional
    public void processScheduledNotifications(LocalTime now) {
        LocalDate today = LocalDate.now();

        // 현재 시각(분 단위까지)과 선호 시간이 일치하고, 오늘 알림을 받지 않은 사용자 목록 조회
        List<UserNotificationSetting> targetSettings = userNotificationSettingRepository.findUsersReadyToSendNotification(now, today);

        for (UserNotificationSetting setting : targetSettings) {
            if(!setting.isEnabled()){
                continue;
            }

            Users user = setting.getUser();

            if(!user.isDeleted()){
                Notification notification = createAndSaveNotification(user);

                sendPushNotification(user, notification);

                setting.updateLastSentDate(today);
                userNotificationSettingRepository.save(setting);
            }
        }
    }

    //알림 읽음 처리
    @Transactional
    public void readNotifications(HttpServletRequest httpServletRequest, NotificationReadRequest requests) {
        String email = tokenUtils.extractUserEmail(httpServletRequest);

        Users user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저는 존재하지 않습니다."));

        Set<Long> notificationIds = new HashSet<>(requests.getNotificationId());

        List<Notification> notificationsToUpdate = notificationRepository.findAllByIdIn(notificationIds);

        for(Notification notification : notificationsToUpdate){
            if (!notification.isRead() && notification.getUser().equals(user)) {
                notification.markAsRead();
            }
        }
    }

    @Transactional(readOnly = true)
    public NotificationResponse getNotifications(HttpServletRequest httpServletRequest) {
        String email = tokenUtils.extractUserEmail(httpServletRequest);

        Users user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다."));

        List<Notification> notifications = notificationRepository.findAllByUser(user);

        List<NotificationItem> items = notifications.stream()
                .map(item ->
                    NotificationItem.builder()
                            .notificationId(item.getId())
                            .title(item.getTitle())
                            .content(item.getContent())
                            .isRead(item.isRead())
                            .build()
                ).toList();

        return NotificationResponse.builder()
                .items(items)
                .unreadCount(notifications.size())
                .build();
    }

    private Notification createAndSaveNotification(Users user) {
        // 알림의 내용과 제목은 필요에 따라 동적으로 생성합니다. (예: 사용자 이름, 오늘의 정보 등)
        String title = "오늘의 질문을 생성해보세요.";
        String content = user.getNickname() + "님, 오늘의 기술 면접 질문은 무엇일까요?";

        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();

        return notificationRepository.save(notification);
    }

    private void sendPushNotification(Users user, Notification notification) {
        // 이 부분은 외부 푸시 서비스(FCM, APNS 등)와의 연동 로직이 들어갑니다.
        // 명세서의 응답 형식에 맞추어 정보를 구성하여 전송합니다.

        externalPushService.send(
                user.getId(),
                notification.getId(),
                notification.getTitle(),
                notification.getContent()
        );
    }
}
