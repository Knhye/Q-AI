package com.example.qnai.repository;

import com.example.qnai.entity.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotebookRepository extends JpaRepository<Notebook, Long> {
}
