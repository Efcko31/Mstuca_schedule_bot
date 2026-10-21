package com.example.mstuca_schedule_bot.config;

import com.example.mstuca_schedule_bot.service.ScheduleApiClient;
import com.example.mstuca_schedule_bot.service.TelegramScheduleBot;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component

public class BotInitializer {
    private static final Logger log = LoggerFactory.getLogger(ScheduleApiClient.class);

    private final TelegramScheduleBot telegramScheduleBot;

    @Autowired
    public BotInitializer(TelegramScheduleBot telegramScheduleBot) {
        this.telegramScheduleBot = telegramScheduleBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(telegramScheduleBot);
        } catch (TelegramApiException e) {
            log.error("Ошибка при инициализации бота: {}", e.getMessage());
        }
    }

}
