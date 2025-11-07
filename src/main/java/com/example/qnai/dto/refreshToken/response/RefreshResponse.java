package com.example.qnai.dto.refreshToken.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RefreshResponse {
    private String refreshToken;
    private String accessToken;
}
