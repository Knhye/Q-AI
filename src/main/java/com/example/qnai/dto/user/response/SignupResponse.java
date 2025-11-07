package com.example.qnai.dto.user.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class SignupResponse {
    private Long userId;
}
