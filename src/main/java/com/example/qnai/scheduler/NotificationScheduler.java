package com.example.qnai.scheduler;

import com.example.qnai.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    // 매분 0초에 실행
    @Scheduled(cron = "0 * * * * *")
    public void sendScheduledNotifications() {
        // 현재 시각을 분 단위까지만 가져옴
        LocalTime nowTime = LocalTime.now().withSecond(0).withNano(0);

        // NotificationService의 알림 처리 메서드 호출
        notificationService.processScheduledNotifications(nowTime);

        // 스케줄러는 예외가 발생해도 다음 스케줄링 주기에 영향을 미치지 않도록 로그만 남기는 것이 일반적입니다.
    }
}