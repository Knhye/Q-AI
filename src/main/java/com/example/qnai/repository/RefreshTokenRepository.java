package com.example.qnai.repository;

import com.example.qnai.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserEmail(String email);

    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);
}
