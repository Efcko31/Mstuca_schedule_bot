package com.example.mstuca_schedule_bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleInitializer {
    private final ScheduleApiClient scheduleApiClient; // Замените на имя вашего сервиса, где лежит метод checkLessonsOnDataBase

    @EventListener(ApplicationReadyEvent.class)
    public void initScheduleOnStartup() {
        log.info("Запуск автоматического обновления расписания при старте приложения...");
        try {
            scheduleApiClient.checkLessonsOnDataBase();
            log.info("Расписание успешно скачано и обновлено в базе данных.");
        } catch (Exception e) {
            log.error("Ошибка при автозагрузке расписания на старте: {}", e.getMessage(), e);
        }
    }
}
