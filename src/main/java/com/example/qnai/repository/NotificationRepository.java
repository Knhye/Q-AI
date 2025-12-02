package com.example.qnai.repository;

import com.example.qnai.entity.Notification;
import com.example.qnai.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByIdInAndUser(Set<Long> ids, Users user);


    List<Notification> findAllByUser(Users user);
}
