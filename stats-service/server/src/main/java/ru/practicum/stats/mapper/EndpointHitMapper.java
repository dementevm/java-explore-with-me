package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mapping(target = "timestamp", expression = "java(parseTimestamp(dto.getTimestamp()))")
    EndpointHit toEntity(EndpointHitDto dto);

    @Mapping(target = "timestamp", expression = "java(formatTimestamp(hit.getTimestamp()))")
    EndpointHitDto toDto(EndpointHit hit);

    default LocalDateTime parseTimestamp(String value) {
        return LocalDateTime.parse(value, FORMATTER);
    }

    default String formatTimestamp(LocalDateTime value) {
        return value.format(FORMATTER);
    }
}
