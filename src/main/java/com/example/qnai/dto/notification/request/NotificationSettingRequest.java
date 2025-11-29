package com.example.qnai.dto.notification.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class NotificationSettingRequest {
    private LocalTime preferredTime;
}
