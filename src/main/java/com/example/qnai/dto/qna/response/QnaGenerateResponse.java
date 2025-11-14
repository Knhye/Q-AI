package com.example.qnai.dto.qna.response;

import com.example.qnai.enums.Level;
import com.example.qnai.enums.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QnaGenerateResponse {
    private Long qnaId;
    private String question;
    private Subject subject;
    private Level level;
}
