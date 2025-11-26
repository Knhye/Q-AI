package com.example.qnai.controller;

import com.example.qnai.common.ApiResponse;
import com.example.qnai.dto.user.request.LogoutRequest;
import com.example.qnai.dto.user.request.UserFcmTokenUpdateRequest;
import com.example.qnai.dto.user.request.UserPasswordUpdateRequest;
import com.example.qnai.dto.user.request.UserUpdateRequest;
import com.example.qnai.dto.user.response.UpdateUserPasswordResponse;
import com.example.qnai.dto.user.response.UserDetailResponse;
import com.example.qnai.dto.user.response.UserUpdateResponse;
import com.example.qnai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User Controller", description = "User Controller API")
public class UserController {
    private final UserService userService;
    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "유저 로그아웃 API")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest httpServletRequest, @Valid @RequestBody LogoutRequest request){
        userService.logout(httpServletRequest, request);
        return ApiResponse.ok("로그아웃이 성공적으로 완료되었습니다.");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get User Detail", description = "유저 정보 상세 조회 API")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(@PathVariable Long id){
        UserDetailResponse response = userService.getUserDetail(id);
        return ApiResponse.ok(response, "회원 정보를 성공적으로 조회하였습니다.");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update User Info", description = "유저 정보 수정 API")
    public ResponseEntity<ApiResponse<UserUpdateResponse>> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request){
        UserUpdateResponse response = userService.updateUser(id, request);
        return ApiResponse.ok(response, "회원 정보를 성공적으로 수정하였습니다.");
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update User Password", description = "유저 비밀번호 수정 API")
    public ResponseEntity<ApiResponse<UpdateUserPasswordResponse>> updateUserPassword(@PathVariable Long id, @Valid @RequestBody UserPasswordUpdateRequest request){
        UpdateUserPasswordResponse response = userService.updateUserPassword(id, request);
        return ApiResponse.ok(response, "비밀번호를 성공적으로 변경하였습니다.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "User Logout", description = "회원 탈퇴 API")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ApiResponse.ok("회원 정보를 성공적으로 삭제하였습니다.");
    }

    @PatchMapping("/fcm")
    @Operation(summary = "Update User's Fcm Token", description = "유저 fcm 토큰 수정 API")
    public ResponseEntity<ApiResponse<Void>> updateUserFcmToken(HttpServletRequest httpServletRequest, @Valid @RequestBody UserFcmTokenUpdateRequest request){
        userService.updateUserFcmToken(httpServletRequest, request);
        return ApiResponse.ok("회원 FCM을 성공적으로 업데이트 하였습니다.");
    }
}
