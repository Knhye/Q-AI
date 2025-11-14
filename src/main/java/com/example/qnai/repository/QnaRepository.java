package com.example.qnai.repository;

import com.example.qnai.entity.QnA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaRepository extends JpaRepository<QnA, Long> {
}
