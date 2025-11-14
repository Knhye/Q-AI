package com.example.qnai.global.handler;

import com.example.qnai.common.ApiResponse;
import com.example.qnai.global.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GlobalExceptionHandler {
    //유저가 이미 존재할 때(동일한 이메일이 존재할 때)
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExistException(UserAlreadyExistException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotValidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotValidTokenException(NotValidTokenException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotLoggedInException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotLoggedInException(NotLoggedInException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyLoggedOutException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyLoggedOutException(UserAlreadyLoggedOutException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAcceptableUserException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotAcceptableUserException(NotAcceptableUserException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AiNoResponseException.class)
    public ResponseEntity<ApiResponse<Void>> handleAiNoResponseException(AiNoResponseException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //요청 값이 올바르지 않을 때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // 커스텀 응답 본문 구조
        Map<String, String> errors = new HashMap<>();

        // 예외 객체에서 BindingResult (오류 목록)를 가져와 반복 처리
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName; // 필드 레벨 오류가 아닐 경우 기본값
            String errorMessage = error.getDefaultMessage();

            // 오류 타입에 따라 필드명 추출
            if (error instanceof FieldError) {
                fieldName = ((FieldError) error).getField();
            } else {
                fieldName = error.getObjectName();
            }

            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTokenException(InvalidTokenException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
