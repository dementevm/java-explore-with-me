package ru.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewm.request.enums.ParticipationRequestStatus;

import java.time.LocalDateTime;

public record ParticipationRequestDto(
        Long id,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        LocalDateTime created,
        Long event,
        Long requester,
        ParticipationRequestStatus status
) {
}
