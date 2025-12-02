package com.example.qnai.global.exception;

public class BadPasswordRequestException extends RuntimeException {
    public BadPasswordRequestException(String message) {
        super(message);
    }
}
