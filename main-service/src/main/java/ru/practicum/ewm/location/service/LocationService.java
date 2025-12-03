package ru.practicum.ewm.location.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.location.dto.NewLocationDto;
import ru.practicum.ewm.location.mapper.LocationMapper;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;

@Service
@AllArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public Location getOrCreateLocation(Float lat, Float lon) {
        return locationRepository.findByLatAndLon(lat, lon)
                .orElse(createLocation(new NewLocationDto(lat, lon)));
    }

    @Transactional
    public Location createLocation(NewLocationDto locationDto) {
        return locationRepository.save(locationMapper.toLocation(locationDto));
    }
}
