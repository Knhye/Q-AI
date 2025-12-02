package com.example.qnai.global.handler;

import com.example.qnai.common.ApiResponse;
import com.example.qnai.global.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //유저가 이미 존재할 때(동일한 이메일이 존재할 때)
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExistException(UserAlreadyExistException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //로그인 되지 않은 사용자일 때
    @ExceptionHandler(NotLoggedInException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotLoggedInException(NotLoggedInException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    //접근이 허용되지 않은 사용자일 때
    @ExceptionHandler(NotAcceptableUserException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotAcceptableUserException(NotAcceptableUserException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    //AI가 응답을 반환하지 않았을 때
    @ExceptionHandler(AiNoResponseException.class)
    public ResponseEntity<ApiResponse<Void>> handleAiNoResponseException(AiNoResponseException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //리소스를 찾지 못했을 때
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //리소스가 일치하지 않을 때
    @ExceptionHandler(ResourceInconsistencyException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceInconsistencyException(ResourceInconsistencyException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //알림을 보낼 수 없을 때
    @ExceptionHandler(CannotSendNotificationException.class)
    public ResponseEntity<ApiResponse<Void>> handleCannotSendNotificationException(CannotSendNotificationException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //검증되지 않은 토큰일 때
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTokenException(InvalidTokenException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    //중복된 리소스가 존재할 때
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResourceException(DuplicateResourceException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.CONFLICT);
    }

    //요청 값이 올바르지 않을 때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        System.out.println(e.getMessage());
        return ApiResponse.fail("요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
    }

    //서버 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e){
        System.out.println(e.getMessage());
        return ApiResponse.fail("서버 오류가 발생하였습니다. : "+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
