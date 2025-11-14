package com.example.qnai.dto.qna.request;

import com.example.qnai.enums.Level;
import com.example.qnai.enums.Subject;
import lombok.Data;

@Data
public class QnaGenerateRequest {
    private Subject subject;
    private Level level;
}
