package com.example.qnai.controller;

import com.example.qnai.common.ApiResponse;
import com.example.qnai.dto.notification.request.SubscribeRequest;
import com.example.qnai.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<Void>> subscribe(HttpServletRequest httpServletRequest, @Valid @RequestBody SubscribeRequest request){
        notificationService.subscribe(httpServletRequest, request);
        return ApiResponse.ok("푸시 알림 설정이 완료되었습니다.");
    }
}
