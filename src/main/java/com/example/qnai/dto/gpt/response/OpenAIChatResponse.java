package com.example.qnai.dto.gpt.response;

import lombok.Data;

import java.util.List;

@Data
public class OpenAIChatResponse {
    private List<Choice> choices;
}
