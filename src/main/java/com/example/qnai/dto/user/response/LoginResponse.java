package com.example.qnai.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String refreshToken;
    private String accessToken;
}
