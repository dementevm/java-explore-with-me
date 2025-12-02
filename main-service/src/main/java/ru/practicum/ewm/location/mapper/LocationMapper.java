package ru.practicum.ewm.location.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.dto.NewLocationDto;
import ru.practicum.ewm.location.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDto toLocationDto(Location location);

    Location toLocation(NewLocationDto locationDto);
}