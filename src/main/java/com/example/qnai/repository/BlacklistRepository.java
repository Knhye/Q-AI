package com.example.qnai.repository;

public interface BlacklistRepository {
    void addToBlacklist(String token, long ttl);
    boolean isBlacklisted(String token);
}
