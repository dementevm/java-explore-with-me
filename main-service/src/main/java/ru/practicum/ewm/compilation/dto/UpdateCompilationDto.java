package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateCompilationDto(
        Set<Integer> events,

        Boolean pinned,

        @Size(min = 1, max = 50)
        String title
) {
}
