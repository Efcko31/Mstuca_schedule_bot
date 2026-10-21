package com.example.mstuca_schedule_bot.repository;

import com.example.mstuca_schedule_bot.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByDate(LocalDate date);

    List<Lesson> findByDateBetween(LocalDate startDay, LocalDate endDay);

    void deleteByDateBetween(LocalDate startDay, LocalDate endDay);
}
