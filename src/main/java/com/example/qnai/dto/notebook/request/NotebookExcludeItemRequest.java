package com.example.qnai.dto.notebook.request;

import lombok.Data;

@Data
public class NotebookExcludeItemRequest {
    private Long notebookId;
    private Long qnaId;
}
