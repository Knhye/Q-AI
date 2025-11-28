package com.example.qnai.global.handler;

import com.example.qnai.common.ApiResponse;
import com.example.qnai.global.exception.*;
import com.google.protobuf.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceInconsistencyException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceInconsistencyException(ResourceInconsistencyException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CannotSendNotificationException.class)
    public ResponseEntity<ApiResponse<Void>> handleCannotSendNotificationException(CannotSendNotificationException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTokenException(InvalidTokenException e){
        System.out.println(e.getMessage());
        return ApiResponse.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e){
        System.out.println(e.getMessage());
        return ApiResponse.fail("서버 오류가 발생하였습니다. : "+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
