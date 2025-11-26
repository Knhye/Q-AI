package com.example.qnai.dto.notebook.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotebookListResponse {
    private Long notebookId;
    private String name;
}
