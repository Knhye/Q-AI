package com.example.qnai.repository;

import com.example.qnai.entity.QnA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaRepository extends JpaRepository<QnA, Long> {
    List<QnA> findAllByUserEmail(String userEmail);
}
