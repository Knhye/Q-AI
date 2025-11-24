package com.example.qnai.dto.qna.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class AnswerUpdateResponse {
    private Long id;
    private String answer;
    private LocalDateTime updatedAt;
}
