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
        LocalTime nowTime = LocalTime.now().withSecond(0).withNano(0);
        notificationService.processScheduledNotifications(nowTime);
    }
}