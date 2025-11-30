package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.location.dto.LocationDto;

public interface UpdateEventBaseDto {
    Long category();

    LocationDto location();
}
