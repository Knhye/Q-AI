package com.example.qnai.controller;


import com.example.qnai.common.ApiResponse;
import com.example.qnai.dto.user.request.SignupRequest;
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
@RequestMapping("/api/user")
public class AuthController {

    private final AuthService authService;
    @PostMapping
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request){
        SignupResponse response = authService.signup(request);
        return ApiResponse.ok(response, "회원가입이 성공적으로 완료되었습니다.");
    }
}
