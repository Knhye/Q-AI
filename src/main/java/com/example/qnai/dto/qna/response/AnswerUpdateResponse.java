package com.example.qnai.dto.qna.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AnswerUpdateResponse {
    private Long id;
    private String answer;
}
