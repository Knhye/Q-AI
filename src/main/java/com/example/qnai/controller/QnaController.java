package com.example.qnai.controller;

import com.example.qnai.common.ApiResponse;
import com.example.qnai.dto.qna.request.AnswerUpdateRequest;
import com.example.qnai.dto.qna.request.FeedbackGenerateRequest;
import com.example.qnai.dto.qna.request.QnaGenerateRequest;
import com.example.qnai.dto.qna.response.*;
import com.example.qnai.service.QnaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna")
public class QnaController {
    private final QnaService qnaService;
    @PostMapping("/question")
    public ResponseEntity<ApiResponse<QnaGenerateResponse>> generateQuestion(HttpServletRequest httpServletRequest, @Valid @RequestBody QnaGenerateRequest request){
        QnaGenerateResponse response = qnaService.generateQuestion(httpServletRequest, request);
        return ApiResponse.ok(response, "질문이 생성되었습니다.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QnaDetailResponse>> getQnaById(HttpServletRequest httpServletRequest, @PathVariable Long id){
        QnaDetailResponse response = qnaService.getQnaById(httpServletRequest, id);
        return ApiResponse.ok(response, "질의응답 조회에 성공하였습니다.");
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<QuestionTitlesResponse>>> getRecentQuestionTitles(HttpServletRequest httpServletRequest){
        List<QuestionTitlesResponse> response = qnaService.getRecentQuestionTitles(httpServletRequest);
        return ApiResponse.ok(response, "최근 질문 타이틀 조회에 성공하였습니다.");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AnswerUpdateResponse>> updateAnswer(HttpServletRequest httpServletRequest, @PathVariable Long id, @Valid @RequestBody AnswerUpdateRequest request){
        AnswerUpdateResponse response = qnaService.updateAnswer(httpServletRequest, id, request);
        return ApiResponse.ok(response, "응답을 저장하였습니다.");
    }

    @PostMapping("/feedback")
    public ResponseEntity<ApiResponse<FeedbackGenerateResponse>> generateFeedback(HttpServletRequest httpServletRequest, @Valid @RequestBody FeedbackGenerateRequest request){
        FeedbackGenerateResponse response = qnaService.generateFeedback(httpServletRequest, request);
        return ApiResponse.ok(response, "피드백이 생성되었습니다.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQna(HttpServletRequest httpServletRequest, @PathVariable Long id){
        qnaService.deleteQna(httpServletRequest, id);
        return ApiResponse.ok("질의응답이 삭제되었습니다.");
    }
}
