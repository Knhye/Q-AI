package com.example.qnai.dto.qna.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionTitlesResponse {
    private Long id;
    private String question;
}
