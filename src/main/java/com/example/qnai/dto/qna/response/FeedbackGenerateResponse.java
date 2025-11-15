package com.example.qnai.dto.qna.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FeedbackGenerateResponse {
    private Long qnaId;
    private String feedback;
}
