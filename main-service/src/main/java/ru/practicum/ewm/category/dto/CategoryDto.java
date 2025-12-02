package ru.practicum.ewm.category.dto;

import jakarta.validation.constraints.Size;


public record CategoryDto(
        Long id,
        @Size(min = 1, max = 50) String name
) {
}
