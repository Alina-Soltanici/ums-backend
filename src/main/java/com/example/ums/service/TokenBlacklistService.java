package com.example.ums.service;


import com.example.ums.security.AccessTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private final RedisTemplate<String, Object> redisTemplate;
    private final AccessTokenService accessTokenService;

    @Value("${jwt.access.expirationMs}")
    private Long expirationAccessTokenMls;

    public void blacklistToken(String token, long expirationSeconds) {
        try {
            log.info("Attempting to blacklist token. Expiration seconds: {}", expirationSeconds);
            log.info("Token (first 20 chars): {}", token.substring(0, Math.min(20, token.length())));

            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = BLACKLIST_PREFIX + token;
            ops.set(key, "true", Duration.ofSeconds(expirationSeconds));

            log.info("Token successfully added to blacklist with key: {}", key.substring(0, Math.min(30, key.length())));

            // Verify it was saved
            Boolean exists = redisTemplate.hasKey(key);
            log.info("Verification - Key exists in Redis: {}", exists);

        } catch (Exception e) {
            log.error("Error blacklisting token", e);
        }
    }

    public boolean isTokenBlackListed(String token) {
        String key = BLACKLIST_PREFIX + token;
        Boolean result = redisTemplate.hasKey(key);
        log.info("Checking if token is blacklisted. Result: {}", result);
        return Boolean.TRUE.equals(result);
    }
}
