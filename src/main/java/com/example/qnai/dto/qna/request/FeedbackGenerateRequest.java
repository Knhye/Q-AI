package com.example.qnai.dto.qna.request;

import lombok.Data;

@Data
public class FeedbackGenerateRequest {
    private Long qnaId;
    private String question;
    private String answer;
}
