package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

public record EventFullDto(
        Long id,
        String annotation,
        CategoryDto category,
        Long confirmedRequests,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdOn,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        UserShortDto initiator,
        LocationDto location,
        Boolean paid,
        Integer participantLimit,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime publishedOn,
        Boolean requestModeration,
        EventState state,
        String title,
        Long views
) {
}

