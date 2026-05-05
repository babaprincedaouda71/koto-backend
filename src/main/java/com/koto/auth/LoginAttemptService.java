package com.koto.auth;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MS = 15 * 60 * 1000L;

    private record AttemptData(int count, long since) {}

    private final ConcurrentHashMap<String, AttemptData> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String email) {
        AttemptData data = attempts.get(email);
        if (data == null || data.count() < MAX_ATTEMPTS) return false;
        if (System.currentTimeMillis() - data.since() > BLOCK_DURATION_MS) {
            attempts.remove(email);
            return false;
        }
        return true;
    }

    public void recordFailure(String email) {
        attempts.merge(email,
                new AttemptData(1, System.currentTimeMillis()),
                (existing, ignored) -> new AttemptData(existing.count() + 1, existing.since()));
    }

    public void reset(String email) {
        attempts.remove(email);
    }
}
