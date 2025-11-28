package com.example.qnai.dto.refreshToken;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RefreshDto {
    private String token;
    private LocalDateTime expiryDatetime;
}
