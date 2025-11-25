package com.example.qnai.repository;

import com.example.qnai.entity.UserNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationSettingRepository extends JpaRepository<UserNotificationSetting, Long> {
}
