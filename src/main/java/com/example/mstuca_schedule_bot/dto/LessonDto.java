package com.example.mstuca_schedule_bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LessonDto {
    private String dayOfWeekString;
    private String date;
    private String beginLesson;
    private String endLesson;
    private String discipline;
    private String building;
    private String kindOfWork;
    private String auditorium;
    private String lecturer;


    public String getDayOfWeekString() {
        return dayOfWeekString;
    }

    public String getDate() {
        return date;
    }

    public String getBeginLesson() {
        return beginLesson;
    }

    public String getEndLesson() {
        return endLesson;
    }

    public String getDiscipline() {
        return discipline;
    }

    public String getKindOfWork() {
        return kindOfWork;
    }

    public String getAuditorium() {
        return auditorium;
    }

    public String getLecturer() {
        return lecturer;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setDayOfWeekString(String dayOfWeekString) {
        this.dayOfWeekString = dayOfWeekString;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setBeginLesson(String beginLesson) {
        this.beginLesson = beginLesson;
    }

    public void setEndLesson(String endLesson) {
        this.endLesson = endLesson;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public void setKindOfWork(String kindOfWork) {
        this.kindOfWork = kindOfWork;
    }

    public void setAuditorium(String auditorium) {
        this.auditorium = auditorium;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }
}
