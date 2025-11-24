package com.example.qnai.dto.notebook.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class NotebookDetailResponse {
    //{
    //  notebookId: number,
    //  name: string,
    //  items:[
    //    title: string,
    //    answer: string,
    //    feedback: string
    //  ]
    //}
    private Long notebookId;
    private String name;
    private List<Item> items;
}
