package com.example.qnai.repository;

import com.example.qnai.entity.UserNotificationSetting;
import com.example.qnai.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserNotificationSettingRepository extends JpaRepository<UserNotificationSetting, Long> {
    Optional<UserNotificationSetting> findByUser(Users user);
}
