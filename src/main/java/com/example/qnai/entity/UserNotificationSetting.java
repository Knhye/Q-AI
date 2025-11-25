package com.example.qnai.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_notification_settings") // 새로운 테이블
public class UserNotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private boolean isEnabled = false;

    //default time : 오전 12시 정각
    @Column(name = "preferred_time", columnDefinition = "TIME", nullable = false)
    @Builder.Default
    private LocalTime preferredTime = LocalTime.MIDNIGHT;

    @Column(name = "last_sent_date")
    private LocalDate lastSentDate;

    public void updatePreferredTime(LocalTime preferredTime) {
        this.preferredTime = preferredTime;
    }
}
