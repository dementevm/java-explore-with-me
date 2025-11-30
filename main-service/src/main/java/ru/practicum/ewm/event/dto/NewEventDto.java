package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.practicum.ewm.location.dto.LocationDto;

import java.time.LocalDateTime;

public record NewEventDto(
        @NotBlank @Size(min = 20, max = 2000)
        String annotation,

        @NotNull
        Long category,

        @NotBlank @Size(min = 20, max = 7000)
        String description,

        @FutureOrPresent @NotNull @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        @NotNull
        LocationDto location,

        @NotBlank @Size(min = 3, max = 120)
        String title,

        Boolean paid,

        Integer participantLimit,

        Boolean requestModeration
) {
}
