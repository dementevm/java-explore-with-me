package ru.practicum.ewm.request.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.practicum.ewm.request.enums.RequestUpdateStatus;

import java.util.Set;

public record EventRequestStatusUpdateRequest(
        @NotEmpty
        Set<Long> requestIds,
        @NotNull
        RequestUpdateStatus status
) {
}
