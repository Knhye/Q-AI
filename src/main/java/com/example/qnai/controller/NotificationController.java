package com.example.qnai.controller;

import com.example.qnai.common.ApiResponse;
import com.example.qnai.dto.notification.request.NotificationSettingRequest;
import com.example.qnai.dto.notification.response.NotificationSettingResponse;
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
}
