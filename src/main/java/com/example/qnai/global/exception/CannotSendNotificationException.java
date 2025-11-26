package com.example.qnai.global.exception;

public class CannotSendNotificationException extends RuntimeException {
    public CannotSendNotificationException(String message) {
        super(message);
    }
}
