package com.OnePassLink.backend.repository;

import com.OnePassLink.backend.model.Secret;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class SecretRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final DefaultRedisScript<String> getAndDeleteScript;

    private static final String SECRET_PREFIX = "secret:";

    public SecretRepository(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;

        // Lua script for atomic GET+DELETE operation
        this.getAndDeleteScript = new DefaultRedisScript<>();
        this.getAndDeleteScript.setScriptText(
            "local value = redis.call('GET', KEYS[1]) " +
            "if value then " +
            "  redis.call('DEL', KEYS[1]) " +
            "  return value " +
            "else " +
            "  return nil " +
            "end"
        );
        this.getAndDeleteScript.setResultType(String.class);
    }

    public void save(Secret secret) {
        try {
            String key = SECRET_PREFIX + secret.getId();
            String value = objectMapper.writeValueAsString(secret);

            Duration ttl = Duration.between(Instant.now(), secret.getExpiresAt());
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save secret", e);
        }
    }

    public Optional<Secret> findAndDelete(String id) {
        try {
            String key = SECRET_PREFIX + id;
            String value = redisTemplate.execute(getAndDeleteScript, List.of(key));

            if (value != null) {
                Secret secret = objectMapper.readValue(value, Secret.class);
                return Optional.of(secret);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve secret", e);
        }
    }

    public boolean exists(String id) {
        String key = SECRET_PREFIX + id;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void delete(String id) {
        String key = SECRET_PREFIX + id;
        redisTemplate.delete(key);
    }
}
