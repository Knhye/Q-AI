package com.example.qnai.dto.notification.request;

import lombok.Data;

import java.time.LocalTime;

@Data
public class NotificationSettingRequest {
    private LocalTime preferredTime;
}
