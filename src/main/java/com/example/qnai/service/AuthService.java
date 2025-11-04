package com.example.qnai.service;

import com.example.qnai.dto.user.request.SignupRequest;
import com.example.qnai.dto.user.response.SignupResponse;
import com.example.qnai.entity.Users;
import com.example.qnai.global.exception.UserAlreadyExistException;
import com.example.qnai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        Users user = userRepository.findByEmail(request.getEmail());

        if(user != null){
            throw new UserAlreadyExistException("이미 존재하는 이메일입니다.");
        }

        Users savedUser = Users.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(request.getPassword())
                .mainSubject(null)
                .notebooks(null)
                .build();

        return SignupResponse.builder()
                .userId(savedUser.getId())
                .build();
    }
}
