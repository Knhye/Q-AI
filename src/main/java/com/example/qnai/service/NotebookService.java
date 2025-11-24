package com.example.qnai.service;

import com.example.qnai.config.TokenProvider;
import com.example.qnai.dto.notebook.request.NotebookCreateRequest;
import com.example.qnai.dto.notebook.response.NotebookCreateResponse;
import com.example.qnai.entity.Notebook;
import com.example.qnai.entity.Users;
import com.example.qnai.global.exception.InvalidTokenException;
import com.example.qnai.global.exception.NotLoggedInException;
import com.example.qnai.repository.NotebookRepository;
import com.example.qnai.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotebookService {
    private final NotebookRepository notebookRepository;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

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
}
