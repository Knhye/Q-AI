package com.example.qnai.dto.qna.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeedbackGenerateResponse {
    private Long qnaId;
    private String feedback;
    private LocalDateTime updatedAt;
}
