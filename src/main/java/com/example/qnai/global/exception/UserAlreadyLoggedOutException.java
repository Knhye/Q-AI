package com.example.qnai.global.exception;

public class UserAlreadyLoggedOutException extends RuntimeException {
    public UserAlreadyLoggedOutException(String message) {
        super(message);
    }
}
