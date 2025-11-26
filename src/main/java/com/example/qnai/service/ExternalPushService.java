package com.example.qnai.service;

public interface ExternalPushService {
    void send(long userId, long notificationId, String title, String content);
}
