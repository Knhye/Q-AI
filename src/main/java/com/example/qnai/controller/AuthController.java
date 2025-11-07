package com.example.qnai.controller;


import com.example.qnai.common.ApiResponse;
import com.example.qnai.dto.refreshToken.response.RefreshResponse;
import com.example.qnai.dto.user.request.LoginRequest;
import com.example.qnai.dto.refreshToken.request.RefreshRequest;
import com.example.qnai.dto.user.request.SignupRequest;
import com.example.qnai.dto.user.response.LoginResponse;
import com.example.qnai.service.AuthService;
import com.example.qnai.dto.user.response.SignupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request){
        SignupResponse response = authService.signup(request);
        return ApiResponse.ok(response, "회원가입이 성공적으로 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);
        return ApiResponse.ok(response, "로그인이 성공적으로 완료되었습니다.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(@Valid @RequestBody RefreshRequest request){
        RefreshResponse response = authService.refresh(request);
        return ApiResponse.ok(response, "액세스 토큰이 성공적으로 발급되었습니다.");
    }
}
