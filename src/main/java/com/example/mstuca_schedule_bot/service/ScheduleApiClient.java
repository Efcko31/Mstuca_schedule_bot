package com.example.mstuca_schedule_bot.service;

import com.example.mstuca_schedule_bot.dto.LessonDto;
import com.example.mstuca_schedule_bot.mapper.LessonMapper;
import com.example.mstuca_schedule_bot.repository.LessonRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ScheduleApiClient {
    private static final Logger log = LoggerFactory.getLogger(ScheduleApiClient.class);

    private final RestTemplate restTemplate;
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    public ScheduleApiClient(RestTemplate restTemplate, LessonRepository lessonRepository, LessonMapper lessonMapper) {
        this.restTemplate = restTemplate;
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
    }

    public List<LessonDto> urlCraftOneWeek(String startDay, String endDay) {

        String url = "https://ruz.mstuca.ru/api/schedule/group/1061?start=" +
                startDay + "&finish=" +
                endDay + "&lng=1";

        int maxAttempts = 3;
        int attempt = 0;

        while (attempt < maxAttempts) {
            try {
                attempt++;
                log.info("Запрос расписания (попытка {}/{}) для периода {} - {}", attempt, maxAttempts, startDay, endDay);

                LessonDto[] response = restTemplate.getForObject(url, LessonDto[].class);
                if (response != null) {
                    return Arrays.asList(response);
                }
                return Collections.emptyList();

            } catch (Exception e) {
                log.warn("Ошибка при запросе к сайту (попытка {}): {}", attempt, e.getMessage());

                if (attempt >= maxAttempts) {
                    log.error("Все попытки исчерпаны. Сайт не отвечает.");
                    throw new RuntimeException("Site unavailable", e);
                }

                try {
                    // Пауза перед следующей попыткой
                    Thread.sleep(1000L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return Collections.emptyList();
    }

    public List<LessonDto> getDataWeekOnWebsite(String week) {
        LocalDate today = LocalDate.now();
        LocalDate startCurrentWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endCurrentWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        if ("next".equals(week)) {
            startCurrentWeek = startCurrentWeek.plusWeeks(1);
            endCurrentWeek = endCurrentWeek.plusWeeks(1);
        }

        return urlCraftOneWeek(
                startCurrentWeek.toString().replaceAll("-", "."),
                endCurrentWeek.toString().replaceAll("-", "."));
    }

    @Transactional
    public void checkLessonsOnDataBase() {
        LocalDate today = LocalDate.now();
        LocalDate startCurrentWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endCurrentWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<LessonDto> currentWeekLessons = urlCraftOneWeek(
                startCurrentWeek.toString().replaceAll("-", "."),
                endCurrentWeek.toString().replaceAll("-", "."));

        List<LessonDto> nextWeekLessons = urlCraftOneWeek(
                startCurrentWeek.plusWeeks(1).toString().replaceAll("-", "."),
                endCurrentWeek.plusWeeks(1).toString().replaceAll("-", "."));

        lessonRepository.deleteByDateBetween(startCurrentWeek, endCurrentWeek);
        lessonRepository.deleteByDateBetween(
                startCurrentWeek.plusWeeks(1),
                endCurrentWeek.plusWeeks(1));

        lessonRepository.saveAll(currentWeekLessons.stream().map(lessonMapper::toEntity).toList());
        lessonRepository.saveAll(nextWeekLessons.stream().map(lessonMapper::toEntity).toList());
    }
}
