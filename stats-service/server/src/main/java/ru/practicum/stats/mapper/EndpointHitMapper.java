package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EndpointHitMapper {
    EndpointHit toEntity(EndpointHitDto dto);

    EndpointHitDto toDto(EndpointHit entity);
}
