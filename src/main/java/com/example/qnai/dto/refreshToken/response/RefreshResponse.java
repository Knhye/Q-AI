package com.example.qnai.dto.refreshToken.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshResponse {
    private String accessToken;
}
