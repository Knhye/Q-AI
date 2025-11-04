package com.example.qnai.config;

import com.example.qnai.entity.Users;
import com.example.qnai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService {
    private final UserRepository userRepository;
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(email);

        if(user == null){
            throw new UsernameNotFoundException("유저가 존재하지 않습니다.");
        }

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
