package com.example.qnai.repository;

import com.example.qnai.entity.UserNotificationSetting;
import com.example.qnai.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface UserNotificationSettingRepository extends JpaRepository<UserNotificationSetting, Long> {
    Optional<UserNotificationSetting> findByUser(Users user);
    Optional<UserNotificationSetting> findByUserEmail(String email);

    //preferredTime == now 이고, lastSentDate < today 인 UserNotificationSetting 조회
    @Query("SELECT uns FROM UserNotificationSetting uns " +
            "WHERE uns.isEnabled = true " +
            "AND (uns.lastSentDate IS NULL OR uns.lastSentDate < :today) " +
            "AND uns.preferredTime = :preferredTime")
    List<UserNotificationSetting> findUsersReadyToSendNotification(
            @Param("preferredTime") LocalTime preferredTime,
            @Param("today") LocalDate today
    );
}
