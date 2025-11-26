package com.example.qnai.dto.notification.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class NotificationSettingResponse {
    private boolean isEnabled;
    private LocalTime preferredTime;
}
