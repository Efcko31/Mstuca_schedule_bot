package com.example.mstuca_schedule_bot.service;

import com.example.mstuca_schedule_bot.dto.LessonDto;
import com.example.mstuca_schedule_bot.mapper.LessonMapper;
import com.example.mstuca_schedule_bot.repository.LessonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Service
public class ScheduleService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final ScheduleApiClient scheduleApiClient;

    public ScheduleService(LessonRepository lessonRepository, LessonMapper lessonMapper, ScheduleApiClient scheduleApiClient) {
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
        this.scheduleApiClient = scheduleApiClient;
    }


    public List<LessonDto> getCurrentOrNextWeek(String week) {
        LocalDate today = LocalDate.now();
        LocalDate startCurrentWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endCurrentWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        if ("next".equals(week)) {
            startCurrentWeek = startCurrentWeek.plusWeeks(1);
            endCurrentWeek = endCurrentWeek.plusWeeks(1);
        }

        List<LessonDto> lessonsWeek = lessonRepository.findByDateBetween(startCurrentWeek, endCurrentWeek)
                .stream()
                .map(lessonMapper::toDto)
                .toList();

        //если по какой-то причине нет в БД данных на необходимую неделю,
        //то в моменте их качает, отправляет и сохраняет в БД
        if (lessonsWeek.isEmpty()) {
            lessonsWeek = scheduleApiClient.getDataWeekOnWebsite(week);
            lessonRepository.saveAll(lessonsWeek.stream().map(lessonMapper::toEntity).toList());
        }

        return lessonsWeek;
    }

    public List<LessonDto> getLessonsOfToday() {

        return lessonRepository.findByDate(LocalDate.now())
                .stream()
                .map(lessonMapper::toDto)
                .toList();
    }

    public List<LessonDto> getLessonsOfTomorrowOrMondayNextWeek() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        List<LessonDto> lessonsDto;

        if (tomorrow.getDayOfWeek().equals(DayOfWeek.SATURDAY) || tomorrow.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            tomorrow = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(1);
            lessonsDto = lessonRepository.findByDate(tomorrow)
                    .stream()
                    .map(lessonMapper::toDto)
                    .toList();
        } else {
            lessonsDto = lessonRepository.findByDate(tomorrow)
                    .stream()
                    .map(lessonMapper::toDto)
                    .toList();
        }

        return lessonsDto;
    }
}
