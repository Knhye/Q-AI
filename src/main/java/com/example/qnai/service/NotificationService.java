package com.example.qnai.service;

import com.example.qnai.dto.fcm.request.MessagePushServiceRequest;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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

    //구독 설정/해지
    @Transactional
    public NotificationSettingResponse notificationSetting(HttpServletRequest httpServletRequest, NotificationSettingRequest request) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

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

        List<UserNotificationSetting> targetSettings =
                userNotificationSettingRepository.findUsersReadyToSendNotification(now, today);

        if (targetSettings.isEmpty()) {
            return;
        }

        // 배치 전송을 위한 요청 리스트
        List<MessagePushServiceRequest> batchRequests = new ArrayList<>();
        List<UserNotificationSetting> settingsToUpdate = new ArrayList<>();

        for (UserNotificationSetting setting : targetSettings) {
            if (!setting.isEnabled()) {
                continue;
            }

            Users user = setting.getUser();

            if (!user.isDeleted()) {
                Notification notification = createAndSaveNotification(user);

                batchRequests.add(MessagePushServiceRequest.of(
                        user.getId(),
                        notification.getId(),
                        notification.getTitle(),
                        notification.getContent()
                ));

                setting.updateLastSentDate(today);
                settingsToUpdate.add(setting);
            }
        }

        // 배치로 한 번에 전송
        if (!batchRequests.isEmpty()) {
            externalPushService.send(batchRequests);
            userNotificationSettingRepository.saveAll(settingsToUpdate);
        }
    }

    //알림 읽음 처리
    @Transactional
    public void readNotifications(HttpServletRequest httpServletRequest, NotificationReadRequest requests) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Users user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저는 존재하지 않습니다."));

        Set<Long> notificationIds = new HashSet<>(requests.getNotificationId());

        List<Notification> notificationsToUpdate =
                notificationRepository.findAllByIdInAndUser(notificationIds, user);

        for (Notification notification : notificationsToUpdate) {
            if (!notification.isRead()) {
                notification.markAsRead();
            }
        }
    }



    @Transactional(readOnly = true)
    public NotificationResponse getNotifications(HttpServletRequest httpServletRequest) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

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
        String title = "오늘의 질문을 생성해보세요.";
        String content = user.getNickname() + "님, 오늘의 기술 면접 질문은 무엇일까요?";

        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();

        return notificationRepository.save(notification);
    }
}
