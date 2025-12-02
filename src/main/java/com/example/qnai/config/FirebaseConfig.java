package com.example.qnai.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    private final ClassPathResource firebaseResource;
    private final String projectId;

    public FirebaseConfig(
            @Value("${fcm.file_path}") String firebaseFilePath,
            @Value("${fcm.project_id}") String projectId
    ) {
        this.firebaseResource = new ClassPathResource(firebaseFilePath);
        this.projectId = projectId;
    }

    @PostConstruct
    public void initialize() {
        try {
            // 이미 초기화되어 있으면 스킵
            if (!FirebaseApp.getApps().isEmpty()) {
                return;
            }

            // 파일 경로가 비어있으면 스킵 (테스트 모드)
            if (!firebaseResource.exists()) {
                return;
            }

            // ClassPathResource에서 InputStream 가져오기
            InputStream serviceAccount = firebaseResource.getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(projectId)
                    .build();

            FirebaseApp.initializeApp(options);


        } catch (IOException e) {
            log.error("Firebase 초기화 실패: {}", e.getMessage(), e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        // Firebase가 초기화되지 않았을 경우를 대비
        if (FirebaseApp.getApps().isEmpty()) {
            return null;
        }
        return FirebaseMessaging.getInstance(firebaseApp());
    }

    @Bean
    public FirebaseApp firebaseApp() {
        if (FirebaseApp.getApps().isEmpty()) {
            return null;
        }
        return FirebaseApp.getInstance();
    }
}