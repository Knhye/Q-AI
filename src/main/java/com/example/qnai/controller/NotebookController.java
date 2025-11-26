package com.example.qnai.controller;

import com.example.qnai.common.ApiResponse;
import com.example.qnai.dto.notebook.request.NotebookAddItemRequest;
import com.example.qnai.dto.notebook.request.NotebookCreateRequest;
import com.example.qnai.dto.notebook.request.NotebookExcludeItemRequest;
import com.example.qnai.dto.notebook.response.NotebookCreateResponse;
import com.example.qnai.dto.notebook.response.NotebookDetailResponse;
import com.example.qnai.dto.notebook.response.NotebookListResponse;
import com.example.qnai.service.NotebookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notebook")
@Tag(name = "Notebook Controller", description = "Notebook Controller API")
public class NotebookController {

    private final NotebookService notebookService;
    @PostMapping
    @Operation(summary = "Create Notebook", description = "노트북 생성 API")
    public ResponseEntity<ApiResponse<NotebookCreateResponse>> createNotebook(HttpServletRequest httpServletRequest, @Valid @RequestBody NotebookCreateRequest request){
        NotebookCreateResponse response = notebookService.createNotebook(httpServletRequest, request);
        return ApiResponse.ok(response, "노트북을 생성하였습니다.");
    }

    @PostMapping("/items")
    @Operation(summary = "Add QnA To Notebook", description = "노트북에 질의응답을 저장하는 API")
    public ResponseEntity<ApiResponse<Void>> addItemToNotebook(HttpServletRequest httpServletRequest, @Valid @RequestBody NotebookAddItemRequest request){
        notebookService.addItemToNotebook(httpServletRequest, request);
        return ApiResponse.ok("노트북에 질의응답을 추가하였습니다.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Notebook", description = "노트북 삭제 API")
    public ResponseEntity<ApiResponse<Void>> deleteNotebook(HttpServletRequest httpServletRequest, @PathVariable Long id){
        notebookService.deleteNotebook(httpServletRequest, id);
        return ApiResponse.ok("노트북을 삭제하였습니다.");
    }

    @DeleteMapping("/items")
    @Operation(summary = "Exclude QnA From Notebook", description = "노트북에서 특정 질의응답을 제외시키는 API")
    public ResponseEntity<ApiResponse<Void>> excludeItemFromNotebook(HttpServletRequest httpServletRequest, @Valid @RequestBody NotebookExcludeItemRequest request){
        notebookService.excludeItemFromNotebook(httpServletRequest, request);
        return ApiResponse.ok("노트북에서 질의응답을 제외하였습니다.");
    }

    @GetMapping
    @Operation(summary = "Get Notebook list", description = "전체 노트북 목록을 조회하는 API")
    public ResponseEntity<ApiResponse<List<NotebookListResponse>>> getNotebookList(HttpServletRequest httpServletRequest){
        List<NotebookListResponse> response = notebookService.getNotebookList(httpServletRequest);
        return ApiResponse.ok(response, "노트북 리스트를 조회하였습니다.");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Notebook Details", description = "노트북 상세 조회 API")
    public ResponseEntity<ApiResponse<NotebookDetailResponse>> getNotebookDetail(HttpServletRequest httpServletRequest, @PathVariable Long id){
        NotebookDetailResponse response = notebookService.getNotebookDetail(httpServletRequest, id);
        return ApiResponse.ok(response, "노트북 질의응답 리스트를 조회하였습니다.");
    }
}
