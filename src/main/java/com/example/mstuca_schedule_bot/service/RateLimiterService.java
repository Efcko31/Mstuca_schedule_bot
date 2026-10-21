package com.example.mstuca_schedule_bot.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RateLimiterService {

    private final Cache<Long, Long> requestCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(2)) //1 запрос в 2 секунды
            .maximumSize(10000) // Максимум пользователей в памяти
            .build();

    public boolean isAllowed(Long userId) {
        long now = System.currentTimeMillis();
        Long lastTime = requestCache.getIfPresent(userId);

        if (lastTime != null && (now - lastTime) < 2000) { //2 секунды
            return false; // Слишком часто! Блокируем этот запрос
        }

        // Обновляем время последнего запроса
        requestCache.put(userId, now);
        return true;
    }
}
