package com.example.qnai.dto.notebook.request;

import lombok.Data;

@Data
public class NotebookAddItemRequest {
    private Long notebookId;
    private Long qnaId;
}
