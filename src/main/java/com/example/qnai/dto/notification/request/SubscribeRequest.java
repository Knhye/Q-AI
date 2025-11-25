package com.example.qnai.dto.notification.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class SubscribeRequest {
    private LocalTime preferredTime;
}
