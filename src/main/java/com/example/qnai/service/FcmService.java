package com.example.qnai.service;

import com.example.qnai.entity.Users;
import com.example.qnai.global.exception.CannotSendNotificationException;
import com.example.qnai.global.exception.ResourceNotFoundException;
import com.example.qnai.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmService implements ExternalPushService{

    private final UserRepository userRepository;

    public void send(
            long userId,
            long notificationId,
            String title,
            String content) {

        // 1. 사용자 ID로 FCM 토큰 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다."));

        String fcmToken = user.getFcmToken();

        if (fcmToken == null || fcmToken.isEmpty()) {
            throw new CannotSendNotificationException("알림을 보낼 수 없습니다. : Fcm token이 존재하지 않음.");
        }

        // 2. FCM 메시지 구성
        Notification firebaseNotification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .build();

        // Data Payload 추가 (옵션): 알림 클릭 시 앱에서 처리할 데이터
        // 예: 어떤 알림인지 구분하기 위한 notificationId
        Message message = Message.builder()
                .setToken(fcmToken) // 대상 디바이스 토큰
                .setNotification(firebaseNotification) // 사용자에게 보여지는 알림
                .putData("notificationId", String.valueOf(notificationId))
                .putData("click_action", "FLUTTER_NOTIFICATION_CLICK") // 앱 설정에 따른 값
                .build();

        // 3. 메시지 전송
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            throw new CannotSendNotificationException("알림을 전송할 수 없습니다.");
        }
    }
}
