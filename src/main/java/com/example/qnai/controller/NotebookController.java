package com.example.qnai.controller;

import com.example.qnai.common.ApiResponse;
import com.example.qnai.dto.notebook.request.NotebookAddItemRequest;
import com.example.qnai.dto.notebook.request.NotebookCreateRequest;
import com.example.qnai.dto.notebook.response.NotebookCreateResponse;
import com.example.qnai.service.NotebookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notebook")
public class NotebookController {

    private final NotebookService notebookService;
    @PostMapping
    public ResponseEntity<ApiResponse<NotebookCreateResponse>> createNotebook(HttpServletRequest httpServletRequest, @Valid @RequestBody NotebookCreateRequest request){
        NotebookCreateResponse response = notebookService.createNotebook(httpServletRequest, request);
        return ApiResponse.ok(response, "노트북을 생성하였습니다.");
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<Void>> addItemToNotebook(HttpServletRequest httpServletRequest, @Valid @RequestBody NotebookAddItemRequest request){
        notebookService.addItemToNotebook(httpServletRequest, request);
        return ApiResponse.ok("노트북에 질의응답을 추가하였습니다.");
    }
}
