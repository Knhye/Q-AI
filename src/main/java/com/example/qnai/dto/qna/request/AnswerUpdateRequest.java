package com.example.qnai.dto.qna.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerUpdateRequest {
    @NotNull
    private String answer;
}
