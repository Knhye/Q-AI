package com.example.qnai.dto.notification.request;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class NotificationReadRequest {
    @Valid
    private List<Long> notificationId;
}
