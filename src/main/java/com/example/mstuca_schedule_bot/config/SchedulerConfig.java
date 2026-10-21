package com.example.mstuca_schedule_bot.config;

import com.example.mstuca_schedule_bot.service.ScheduleApiClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class SchedulerConfig {
    private final ScheduleApiClient scheduleApiClient;

    public SchedulerConfig(ScheduleApiClient scheduleApiClient) {
        this.scheduleApiClient = scheduleApiClient;
    }

    //метод не должен ничего принимать и ничего не возвращать
    @Scheduled(cron = "${bot.schedule.cron}", zone = "Europe/Moscow")
    public void chekScheduleChanges() {
        scheduleApiClient.checkLessonsOnDataBase();
    }
}
