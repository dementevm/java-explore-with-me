package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.ewm.event.enums.UserActionState;
import ru.practicum.ewm.location.dto.LocationDto;

import java.time.LocalDateTime;

public record UpdateEventUserDto(
        @Size(min = 20, max = 2000)
        String annotation,

        Long category,

        @Size(min = 20, max = 7000)
        String description,

        @FutureOrPresent
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        LocationDto location,

        Boolean paid,

        @PositiveOrZero
        Integer participantLimit,

        Boolean requestModeration,

        UserActionState stateAction,

        @Size(min = 3, max = 120)
        String title
) implements UpdateEventBaseDto {
}
