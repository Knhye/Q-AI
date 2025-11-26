package com.example.qnai.dto.notification.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NotificationResponse {
    private List<NotificationItem> items;
    private Integer unreadCount;
}
