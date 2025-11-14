package com.example.qnai.global.exception;

public class AiNoResponseException extends RuntimeException {
    public AiNoResponseException(String message) {
        super(message);
    }
}
