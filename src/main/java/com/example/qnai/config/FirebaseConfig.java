package com.example.qnai.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {
    @PostConstruct
    public void initialize() throws IOException {

        // 1. 서비스 계정 파일 로드 (파일명은 실제 파일명으로 변경하세요)
        ClassPathResource resource = new ClassPathResource("firebase/qnai-firebase-service-account.json");

        // 2. Firebase 옵션 설정
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                // (Optional) Database URL, Storage Bucket 등 추가 설정 가능
                .build();

        // 3. Firebase 앱 초기화
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
