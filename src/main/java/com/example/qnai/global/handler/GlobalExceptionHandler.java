package com.example.qnai.global.handler;

import com.example.qnai.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
