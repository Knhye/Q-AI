package com.example.qnai.dto.fcm.request;

import lombok.Builder;

@Builder
public record MessagePushServiceRequest(
         Long userId,
         Long notificationId,
         String title,
         String body
        ) {
    public static MessagePushServiceRequest of(
            Long userId,
            Long notificationId,
            String title,
            String body
    ) {
        return MessagePushServiceRequest.builder()
                .userId(userId)
                .notificationId(notificationId)
                .title(title)
                .body(body)
                .build();
    }
}
