package com.example.qnai.entity;

import com.example.qnai.enums.Subject;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    private Subject mainSubject;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notebook> notebooks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<QnA> qnaList;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private RefreshToken refreshToken;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private UserNotificationSetting notificationSetting;

    @Column(name = "fcm_token")
    private String fcmToken;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    public void updateInfo(String email, String nickname, Subject mainSubject) {
        this.email = email;
        this.nickname = nickname;
        this.mainSubject = mainSubject;
    }

    public void updatePassword(String newHashedPassword) {
        this.password = newHashedPassword;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
