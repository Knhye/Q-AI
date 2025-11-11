package com.example.qnai.dto.user.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPasswordUpdateRequest {
    private String currentPassword;
    private String newPassword;
}
