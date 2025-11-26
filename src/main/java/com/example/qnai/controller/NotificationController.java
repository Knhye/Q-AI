package com.example.qnai.controller;

import com.example.qnai.common.ApiResponse;
import com.example.qnai.dto.notification.request.NotificationReadRequest;
import com.example.qnai.dto.notification.request.NotificationSettingRequest;
import com.example.qnai.dto.notification.response.NotificationResponse;
import com.example.qnai.dto.notification.response.NotificationSettingResponse;
import com.example.qnai.entity.Notification;
import com.example.qnai.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
@Tag(name = "Notification Controller", description = "Notification Controller API")
public class NotificationController {
    private final NotificationService notificationService;
    @PatchMapping("/settings")
    @Operation(summary = "Set Notification", description = "푸시 알림 구독/해지 API")
    public ResponseEntity<ApiResponse<NotificationSettingResponse>> notificationSetting(HttpServletRequest httpServletRequest, @Valid @RequestBody NotificationSettingRequest request){
        NotificationSettingResponse response = notificationService.notificationSetting(httpServletRequest, request);
        return ApiResponse.ok(response, "푸시 알림 설정이 변경되었습니다.");
    }

    @PatchMapping
    @Operation(summary = "Read Notifications", description = "알림 읽음 처리 API")
    public ResponseEntity<ApiResponse<Void>> readNotifications(HttpServletRequest httpServletRequest, @Valid @RequestBody NotificationReadRequest requests){
        notificationService.readNotifications(httpServletRequest, requests);
        return ApiResponse.ok("알림 읽음 처리가 완료되었습니다.");
    }

    @GetMapping
    @Operation(summary = "Get Notifications", description = "알림 목록 조회 API")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotifications(HttpServletRequest httpServletRequest){
        NotificationResponse response = notificationService.getNotifications(httpServletRequest);
        return ApiResponse.ok(response, "알림 목록을 성공적으로 조회하였습니다.");
    }
}
