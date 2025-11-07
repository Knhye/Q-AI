package com.example.qnai.dto.user.response;

import com.example.qnai.enums.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDetailResponse {
    private Long userId;
    private String email;
    private String nickname;
    private Subject mainSubject;
}
