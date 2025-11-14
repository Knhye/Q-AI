package com.example.qnai.dto.gpt.response;

import lombok.Data;

@Data
public class Message {
    private String role;
    private String content;
}
