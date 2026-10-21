package com.example.mstuca_schedule_bot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lessons", schema = "mstuca_schedule_bot_schema")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_of_week", nullable = false, length = 255)
    private String dayOfWeekString;

    @Column(name = "date", nullable = false, length = 50)
    private LocalDate date;

    @Column(name = "begin_lesson", nullable = false, length = 50)
    private String beginLesson;

    @Column(name = "end_lesson", nullable = false, length = 50)
    private String endLesson;

    @Column(name = "discipline", nullable = false, length = 255)
    private String discipline;

    @Column(name = "building", nullable = false, length = 255)
    private String building;

    @Column(name = "kind_of_work", nullable = false, length = 255)
    private String kindOfWork;

    @Column(name = "auditorium", nullable = false, length = 255)
    private String auditorium;

    @Column(name = "lecturer", nullable = false, length = 255)
    private String lecturer;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getAuditorium() {
        return auditorium;
    }

    public void setAuditorium(String auditorium) {
        this.auditorium = auditorium;
    }

    public String getKindOfWork() {
        return kindOfWork;
    }

    public void setKindOfWork(String kindOfWork) {
        this.kindOfWork = kindOfWork;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public String getEndLesson() {
        return endLesson;
    }

    public void setEndLesson(String endLesson) {
        this.endLesson = endLesson;
    }

    public String getBeginLesson() {
        return beginLesson;
    }

    public void setBeginLesson(String beginLesson) {
        this.beginLesson = beginLesson;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDayOfWeekString() {
        return dayOfWeekString;
    }

    public void setDayOfWeekString(String dayOfWeekString) {
        this.dayOfWeekString = dayOfWeekString;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
