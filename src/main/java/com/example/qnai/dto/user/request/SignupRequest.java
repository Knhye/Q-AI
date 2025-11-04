package com.example.qnai.dto.user.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class SignupRequest {
    @NotNull
    private String email;

    @NotNull
    private String nickname;

    @NotNull
    private String password;
}
