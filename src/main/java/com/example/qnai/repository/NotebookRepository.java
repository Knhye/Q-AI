package com.example.qnai.repository;

import com.example.qnai.entity.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotebookRepository extends JpaRepository<Notebook, Long> {
    Optional<Notebook> findByIdAndIsDeletedFalse(Long id);
    List<Notebook> findAllByUserEmailAndIsDeletedFalse(String s);
}
