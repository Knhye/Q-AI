package com.example.qnai.service;

import com.example.qnai.config.TokenProvider;
import com.example.qnai.dto.qna.request.AnswerUpdateRequest;
import com.example.qnai.dto.qna.request.QnaGenerateRequest;
import com.example.qnai.dto.qna.response.AnswerUpdateResponse;
import com.example.qnai.dto.qna.response.QnaDetailResponse;
import com.example.qnai.dto.qna.response.QnaGenerateResponse;
import com.example.qnai.dto.qna.response.QuestionTitlesResponse;
import com.example.qnai.entity.QnA;
import com.example.qnai.entity.Users;
import com.example.qnai.global.exception.*;
import com.example.qnai.repository.QnaRepository;
import com.example.qnai.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaService {
    private final QnaRepository qnaRepository;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final GptOssService gptOssService;

    private String extractUserEmail(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new NotLoggedInException("로그인이 필요한 요청입니다.");
        }

        String accessToken = bearerToken.substring(7); // "Bearer " 제거

        if (!tokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException("유효하지 않은 Access Token입니다.");
        }

        String email = tokenProvider.extractUsername(accessToken);

        if(email.isEmpty()){
            throw new UsernameNotFoundException("이메일을 추출할 수 없습니다.");
        }

        return email;
    }

    @Transactional
    public QnaGenerateResponse generateQuestion(HttpServletRequest httpServletRequest, QnaGenerateRequest request) {
        String email = extractUserEmail(httpServletRequest);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        QnaGenerateResponse response = gptOssService.generateQuestion(request);

        if(response == null){
            throw new AiNoResponseException("AI로부터 응답을 받지 못했습니다.");
        }

        QnA newQna = QnA.builder()
                .question(response.getQuestion())
                .answer(null)
                .createdAt(LocalDateTime.now())
                .feedback(null)
                .level(request.getLevel())
                .subject(request.getSubject())
                .user(user)
                .build();

        qnaRepository.save(newQna);

        return QnaGenerateResponse.builder()
                .qnaId(newQna.getId())
                .question(response.getQuestion())
                .subject(response.getSubject())
                .level(response.getLevel())
                .build();
    }

    @Transactional(readOnly = true)
    public QnaDetailResponse getQnaById(HttpServletRequest httpServletRequest, Long id) {
        QnA qnA = qnaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 질의응답이 존재하지 않습니다."));

        String email = extractUserEmail(httpServletRequest);
        if(!email.equals(qnA.getUser().getEmail())){
            throw new NotAcceptableUserException("다른 유저의 질의응답은 조회할 수 없습니다.");
        }

        return QnaDetailResponse.builder()
                .id(qnA.getId())
                .question(qnA.getQuestion())
                .answer(qnA.getAnswer())
                .feedback(qnA.getFeedback())
                .subject(qnA.getSubject())
                .level(qnA.getLevel())
                .build();

    }

    @Transactional(readOnly = true)
    public List<QuestionTitlesResponse> getRecentQuestionTitles(HttpServletRequest httpServletRequest) {

        List<QnA> qnAList = qnaRepository.findAllByUserEmail(
                extractUserEmail(httpServletRequest)
        );

        return qnAList.stream()
                .map(qna -> QuestionTitlesResponse.builder()
                        .id(qna.getId())
                        .question(qna.getQuestion())
                        .build()
                )
                .toList();
    }

    @Transactional
    public AnswerUpdateResponse updateAnswer(HttpServletRequest httpServletRequest, Long id, AnswerUpdateRequest request) {
        QnA qnA = qnaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 질의응답이 존재하지 않습니다."));

        String email = extractUserEmail(httpServletRequest);

        if(!email.equals(qnA.getUser().getEmail())){
            throw new NotAcceptableUserException("다른 유저의 질의응답은 수정할 수 없습니다.");
        }

        qnA.setAnswer(request.getAnswer());
        qnaRepository.save(qnA);

        return AnswerUpdateResponse.builder()
                .id(qnA.getId())
                .answer(qnA.getAnswer())
                .build();
    }
}
