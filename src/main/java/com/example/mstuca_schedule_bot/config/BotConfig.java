package com.example.mstuca_schedule_bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource("application.yaml")
public class BotConfig {
    @Value(value = "${bot.name}")
    String botName;
    @Value(value = "${bot.token}")
    String botToken;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getBotName() {
        return botName;
    }

    public String getBotToken() {
        return botToken;
    }
}
