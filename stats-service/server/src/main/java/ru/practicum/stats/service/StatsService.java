package ru.practicum.stats.service;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.mapper.EndpointHitMapper;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper;

    @Transactional
    public EndpointHitDto createEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit saved = statsRepository.save(endpointHitMapper.toEntity(endpointHitDto));
        return endpointHitMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ViewStatsDto> getEndpointHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new ValidationException("End must be after start");
        }

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statsRepository.getStatsUnique(start, end);
            } else {
                return statsRepository.getStats(start, end);
            }
        }

        if (unique) {
            return statsRepository.getUrisStatsUnique(start, end, uris);
        } else {
            return statsRepository.getUrisStats(start, end, uris);
        }
    }

}
