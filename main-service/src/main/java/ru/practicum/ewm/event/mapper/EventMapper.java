package ru.practicum.ewm.event.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.location.mapper.LocationMapper;
import ru.practicum.ewm.user.model.User;

@Mapper(componentModel = "spring", uses = {LocationMapper.class, CategoryMapper.class, CommentMapper.class})
public interface EventMapper {
    EventFullDto toEventFullDto(Event event);

    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paid", source = "dto.paid", defaultValue = "false")
    @Mapping(target = "participantLimit", source = "dto.participantLimit", defaultValue = "0")
    @Mapping(target = "requestModeration", source = "dto.requestModeration", defaultValue = "true")
    @Mapping(target = "category", ignore = true)
    Event toEvent(NewEventDto dto, User initiator);

    Event toEvent(EventFullDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    void updateEventFromAdminDto(UpdateEventAdminDto dto, @MappingTarget Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    void updateEventFromUserDto(UpdateEventUserDto dto, @MappingTarget Event event);

}
