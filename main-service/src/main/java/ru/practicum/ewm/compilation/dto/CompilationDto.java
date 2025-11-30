package ru.practicum.ewm.compilation.dto;

import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.Set;

public record CompilationDto(
        Long id,
        Boolean pinned,
        String title,
        Set<EventShortDto> events
) {
}
