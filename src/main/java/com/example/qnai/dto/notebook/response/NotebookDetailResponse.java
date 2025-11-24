package com.example.qnai.dto.notebook.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class NotebookDetailResponse {
    private Long notebookId;
    private String name;
    private List<Item> items;
}
