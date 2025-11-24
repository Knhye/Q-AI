package com.example.qnai.repository;

import com.example.qnai.entity.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotebookRepository extends JpaRepository<Notebook, Long> {
    List<Notebook> findAllByUserEmail(String s);
}
