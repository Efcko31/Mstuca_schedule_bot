package com.example.mstuca_schedule_bot.mapper;

import com.example.mstuca_schedule_bot.dto.LessonDto;
import com.example.mstuca_schedule_bot.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LessonMapper {

    LessonDto toDto (Lesson lesson);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.OffsetDateTime.now())")
    Lesson toEntity(LessonDto lessonDto);
}
