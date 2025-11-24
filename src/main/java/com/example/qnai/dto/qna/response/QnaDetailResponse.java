package com.example.qnai.dto.qna.response;

import com.example.qnai.enums.Level;
import com.example.qnai.enums.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class QnaDetailResponse {
    private Long id;
    private String question;
    private String answer;
    private String feedback;
    private Subject subject;
    private Level level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
