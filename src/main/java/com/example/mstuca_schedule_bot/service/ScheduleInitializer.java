package com.example.mstuca_schedule_bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component

public class ScheduleInitializer {
    private static final Logger log = LoggerFactory.getLogger(ScheduleApiClient.class);
    private final ScheduleApiClient scheduleApiClient;

    public ScheduleInitializer(ScheduleApiClient scheduleApiClient) {
        this.scheduleApiClient = scheduleApiClient;
    }

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
