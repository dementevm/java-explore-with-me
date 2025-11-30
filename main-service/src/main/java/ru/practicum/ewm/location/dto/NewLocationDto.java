package ru.practicum.ewm.location.dto;

import jakarta.validation.constraints.NotNull;


public record NewLocationDto(
        @NotNull
        Float lat,
        @NotNull
        Float lon
) {
}
