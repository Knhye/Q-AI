package com.example.qnai.dto.notification.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class NotificationSettingResponse {
    private Long userId;
    private boolean isEnabled;
    private LocalTime preferredTime;
}
