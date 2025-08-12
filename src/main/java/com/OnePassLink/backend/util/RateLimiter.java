package com.OnePassLink.backend.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RateLimiter {

    private final RedisTemplate<String, String> redisTemplate;
    private final DefaultRedisScript<Long> rateLimitScript;

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    public RateLimiter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;

        // Lua script for atomic rate limiting with sliding window
        this.rateLimitScript = new DefaultRedisScript<>();
        this.rateLimitScript.setScriptText(
            "local key = KEYS[1] " +
            "local limit = tonumber(ARGV[1]) " +
            "local window = tonumber(ARGV[2]) " +
            "local current = redis.call('GET', key) " +
            "if current == false then " +
            "  redis.call('SET', key, 1) " +
            "  redis.call('EXPIRE', key, window) " +
            "  return 1 " +
            "end " +
            "current = tonumber(current) " +
            "if current < limit then " +
            "  return redis.call('INCR', key) " +
            "else " +
            "  return -1 " +
            "end"
        );
        this.rateLimitScript.setResultType(Long.class);
    }

    /**
     * Check if request is allowed based on IP rate limiting
     * @param clientIp The client IP address
     * @param limit Maximum requests allowed
     * @param windowSeconds Time window in seconds
     * @return true if allowed, false if rate limited
     */
    public boolean isAllowed(String clientIp, int limit, int windowSeconds) {
        String key = RATE_LIMIT_PREFIX + clientIp;
        Long result = redisTemplate.execute(rateLimitScript,
            List.of(key),
            String.valueOf(limit),
            String.valueOf(windowSeconds)
        );

        return result != null && result != -1;
    }

    /**
     * Get current request count for IP
     * @param clientIp The client IP address
     * @return current count or 0 if not found
     */
    public long getCurrentCount(String clientIp) {
        String key = RATE_LIMIT_PREFIX + clientIp;
        String count = redisTemplate.opsForValue().get(key);
        return count != null ? Long.parseLong(count) : 0;
    }

    /**
     * Get remaining TTL for rate limit key
     * @param clientIp The client IP address
     * @return TTL in seconds, -1 if key doesn't exist
     */
    public long getTTL(String clientIp) {
        String key = RATE_LIMIT_PREFIX + clientIp;
        return redisTemplate.getExpire(key);
    }
}
