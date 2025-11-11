package com.example.qnai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 데이터 저장
    public void save(String key, Object value, long expirationMillis) {
        Duration duration = Duration.ofMillis(expirationMillis);
        redisTemplate.opsForValue().set(key, value, duration);
    }
}

