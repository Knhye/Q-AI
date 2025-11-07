package com.example.qnai.controller;

import com.example.qnai.common.ApiResponse;
import com.example.qnai.dto.user.request.LogoutRequest;
import com.example.qnai.dto.user.response.UserDetailResponse;
import com.example.qnai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request){
        userService.logout(request);
        return ApiResponse.ok("로그아웃이 성공적으로 완료되었습니다.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(@Valid @PathVariable Long id){
        UserDetailResponse response = userService.getUserDetail(id);
        return ApiResponse.ok(response, "회원 정보를 성공적으로 조회하였습니다.");
    }
}
