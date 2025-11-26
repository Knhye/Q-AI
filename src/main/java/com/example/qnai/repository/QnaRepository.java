package com.example.qnai.repository;

import com.example.qnai.entity.Notebook;
import com.example.qnai.entity.QnA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QnaRepository extends JpaRepository<QnA, Long> {
    Optional<QnA> findByIdAndIsDeletedFalse(Long id);

    List<QnA> findAllByNotebook(Notebook notebook);

    List<QnA> findAllByUserEmailOrderByUpdatedAtDesc(String s);
}
