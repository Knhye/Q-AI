package com.example.qnai.dto.notification.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationItem {
    private Long notificationId;
    private String title;
    private String content;
    private boolean isRead;
}
