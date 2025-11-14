package com.example.qnai.controller;

import com.example.qnai.common.ApiResponse;
import com.example.qnai.dto.qna.request.QnaGenerateRequest;
import com.example.qnai.dto.qna.response.QnaGenerateResponse;
import com.example.qnai.service.QnaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna")
public class QnaController {
    private final QnaService qnaService;
    @PostMapping
    public ResponseEntity<ApiResponse<QnaGenerateResponse>> generateQuestion(HttpServletRequest httpServletRequest, @Valid @RequestBody QnaGenerateRequest request){
        QnaGenerateResponse response = qnaService.generateQuestion(httpServletRequest, request);
        return ApiResponse.ok(response, "질문이 생성되었습니다.");
    }
}
