package com.example.qnai.controller;

import com.example.qnai.common.ApiResponse;
import com.example.qnai.dto.notification.request.NotificationReadRequest;
import com.example.qnai.dto.notification.request.NotificationSettingRequest;
import com.example.qnai.dto.notification.response.NotificationResponse;
import com.example.qnai.dto.notification.response.NotificationSettingResponse;
import com.example.qnai.entity.Notification;
import com.example.qnai.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;
    @PatchMapping("/settings")
    public ResponseEntity<ApiResponse<NotificationSettingResponse>> notificationSetting(HttpServletRequest httpServletRequest, @Valid @RequestBody NotificationSettingRequest request){
        NotificationSettingResponse response = notificationService.notificationSetting(httpServletRequest, request);
        return ApiResponse.ok(response, "푸시 알림 설정이 변경되었습니다.");
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> readNotifications(HttpServletRequest httpServletRequest, @Valid @RequestBody NotificationReadRequest requests){
        notificationService.readNotifications(httpServletRequest, requests);
        return ApiResponse.ok("알림 읽음 처리가 완료되었습니다.");
    }

//    @GetMapping
//    public ResponseEntity<ApiResponse<NotificationResponse>> getNotifications(HttpServletRequest httpServletRequest){
//        NotificationResponse response = notificationService.getNotifications(httpServletRequest);
//        return ApiResponse.ok(response, "알림 목록을 성공적으로 조회하였습니다.");
//    }
}
