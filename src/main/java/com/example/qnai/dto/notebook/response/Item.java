package com.example.qnai.dto.notebook.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Item {
    private Long qnaId;
    private String question;
    private String answer;
    private String feedback;
}
