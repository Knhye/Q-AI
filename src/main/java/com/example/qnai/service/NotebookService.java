package com.example.qnai.service;

import com.example.qnai.config.TokenProvider;
import com.example.qnai.dto.notebook.request.NotebookAddItemRequest;
import com.example.qnai.dto.notebook.request.NotebookCreateRequest;
import com.example.qnai.dto.notebook.request.NotebookExcludeItemRequest;
import com.example.qnai.dto.notebook.response.NotebookCreateResponse;
import com.example.qnai.entity.Notebook;
import com.example.qnai.entity.QnA;
import com.example.qnai.entity.Users;
import com.example.qnai.global.exception.InvalidTokenException;
import com.example.qnai.global.exception.NotAcceptableUserException;
import com.example.qnai.global.exception.NotLoggedInException;
import com.example.qnai.global.exception.ResourceNotFoundException;
import com.example.qnai.repository.NotebookRepository;
import com.example.qnai.repository.QnaRepository;
import com.example.qnai.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotebookService {
    private final NotebookRepository notebookRepository;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final QnaRepository qnaRepository;

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
    public NotebookCreateResponse createNotebook(HttpServletRequest httpServletRequest, NotebookCreateRequest request) {
        String email = extractUserEmail(httpServletRequest);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않습니다."));

        Notebook notebook = Notebook.builder()
                .name(request.getName())
                .user(user)
                .qnAs(null)
                .build();

        notebookRepository.save(notebook);

        return NotebookCreateResponse.builder()
                .notebookId(notebook.getId())
                .name(notebook.getName())
                .build();
    }

    @Transactional
    public void addItemToNotebook(HttpServletRequest httpServletRequest, NotebookAddItemRequest request) {
        Notebook notebook = notebookRepository.findById(request.getNotebookId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 노트북이 존재하지 않습니다."));

        if(notebook.isDeleted()){
            throw new ResourceNotFoundException("삭제된 노트북입니다.");
        }

        QnA qnA = qnaRepository.findById(request.getQnaId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 질의응답이 존재하지 않습니다."));

        String email = extractUserEmail(httpServletRequest);

        if(!Objects.equals(notebook.getUser().getEmail(), email) || !Objects.equals(qnA.getUser().getEmail(), email)){
            throw new NotAcceptableUserException("다른 유저의 노트북 또는 질의응답에 접근할 수 없습니다.");
        }

        //노트북 qna 리스트에 추가
        notebook.getQnAs().add(qnA);

        //qna 노트북 설정
        qnA.setNotebook(notebook);
    }

    @Transactional
    public void deleteNotebook(HttpServletRequest httpServletRequest, Long id) {
        Notebook notebook = notebookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 노트북이 존재하지 않습니다."));

        if(notebook.isDeleted()){
            throw new ResourceNotFoundException("삭제된 노트북입니다.");
        }

        String email = extractUserEmail(httpServletRequest);

        if(!notebook.getUser().getEmail().equals(email)){
            throw new NotAcceptableUserException("다른 유저의 노트북에 접근할 수 없습니다.");
        }

        notebook.delete();
    }

    @Transactional
    public void excludeItemFromNotebook(HttpServletRequest httpServletRequest, NotebookExcludeItemRequest request) {
        Notebook notebook = notebookRepository.findById(request.getNotebookId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 노트북이 존재하지 않습니다."));

        QnA qnA = qnaRepository.findById(request.getQnaId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 질의응답이 존재하지 않습니다."));

        String email = extractUserEmail(httpServletRequest);

        if(!notebook.getUser().getEmail().equals(email) || !qnA.getUser().getEmail().equals(email)){
            throw new NotAcceptableUserException("다른 유저의 노트북 또는 질의응답에 접근할 수 없습니다.");
        }

        if (!notebook.getQnAs().contains(qnA)) {
            throw new ResourceNotFoundException("해당 QnA는 현재 노트북에 포함되어 있지 않습니다.");
        }

        notebook.getQnAs().remove(qnA);

        qnA.setNotebook(null);
    }
}
