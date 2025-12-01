package ru.practicum.ewm.event.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.enums.AdminActionState;
import ru.practicum.ewm.event.enums.EventSort;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.EventUpdateException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.service.LocationService;
import ru.practicum.ewm.request.enums.ParticipationRequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final LocationService locationService;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryService categoryService;
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;

    @Transactional(readOnly = true)
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event with id %d not found".formatted(eventId)));
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        userService.getUserById(userId);
        return eventRepository.findByUserIdWithOffsetAndLimit(userId, from, size).stream()
                .map(eventMapper::toEventShortDto).toList();
    }

    @Transactional
    public EventFullDto createEvent(NewEventDto dto, Long userId) {
        User user = userMapper.toUser(userService.getUserById(userId));
        Category category = categoryMapper.toCategory(categoryService.getCategory(dto.category()));
        Location location = locationService.getOrCreateLocation(dto.location().lat(), dto.location().lon());
        Event event = eventMapper.toEvent(dto, user);
        event.setLocation(location);
        event.setCategory(category);
        Event saved = eventRepository.save(event);
        return eventMapper.toEventFullDto(saved);
    }

    @Transactional(readOnly = true)
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        userService.getUserById(userId);
        Event event = getEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ObjectNotFoundException("Event with id %d not found".formatted(eventId));
        }

        return eventMapper.toEventFullDto(event);
    }

    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserDto dto) {
        userService.getUserById(userId);
        Event event = getEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ObjectNotFoundException("Event with id %d not found".formatted(eventId));
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new EventUpdateException("Only pending or canceled events can be changed");
        }

        LocalDateTime newDate = dto.eventDate();
        if (newDate != null && newDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventUpdateException("Time of updated event can't be before 2 hours from now");
        }

        hydrateUpdateEvent(event, dto);

        if (dto.stateAction() != null) {
            switch (dto.stateAction()) {
                case SEND_TO_REVIEW -> {
                    if (event.getState() != EventState.CANCELED && event.getState() != EventState.PENDING) {
                        throw new EventUpdateException("Only pending or canceled events can be changed");
                    }
                    event.setState(EventState.PENDING);
                }
                case CANCEL_REVIEW -> {
                    event.setState(EventState.CANCELED);
                }
            }
        }

        eventMapper.updateEventFromUserDto(dto, event);
        Event saved = eventRepository.save(event);
        return eventMapper.toEventFullDto(saved);
    }

    @Transactional(readOnly = true)
    public List<EventFullDto> getAdminEvents(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size
    ) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAdminEvents(users, states, categories, rangeStart, rangeEnd, pageable);

        events.forEach(this::hydrateConfirmedRequests);

        return events.stream()
                .map(eventMapper::toEventFullDto)
                .toList();
    }

    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminDto dto) {
        Event event = getEventById(eventId);

        if (dto.eventDate() != null && event.getPublishedOn() != null) {
            if (dto.eventDate().isBefore(event.getPublishedOn().plusHours(1))) {
                throw new EventUpdateException("Event date must be at least 1 hour after publication date");
            }
        }

        hydrateUpdateEvent(event, dto);

        if (dto.stateAction() != null) {
            if (dto.stateAction() == AdminActionState.PUBLISH_EVENT) {
                if (event.getState() != null && event.getState() != EventState.PENDING) {
                    throw new EventUpdateException("Cannot publish the event because it's not in the right state: %s".formatted(event.getState()));
                }
                LocalDateTime eventDate = dto.eventDate() != null ? dto.eventDate() : event.getEventDate();
                if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new EventUpdateException("Event date must be at least 1 hour after now");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (dto.stateAction() == AdminActionState.REJECT_EVENT) {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new EventUpdateException("Cannot reject the event because it's already published");
                }
                event.setState(EventState.CANCELED);
            }
        }

        eventMapper.updateEventFromAdminDto(dto, event);
        Event saved = eventRepository.save(event);
        return eventMapper.toEventFullDto(saved);
    }

    private void hydrateUpdateEvent(
            Event event, UpdateEventBaseDto dto
    ) {
        if (dto.category() != null) {
            Category category = categoryMapper.toCategory(categoryService.getCategory(dto.category()));
            event.setCategory(category);
        }
        if (dto.location() != null) {
            Location location = locationService.getOrCreateLocation(dto.location().lat(), dto.location().lon());
            event.setLocation(location);
        }
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            EventSort sort,
            Integer from,
            Integer size
    ) {
        String textPattern = null;
        if (text != null && !text.isBlank()) {
            textPattern = "%" + text.toLowerCase() + "%";
        }

        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("rangeEnd must be after rangeStart");
        }

        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
        }

        Pageable pageable = switch (sort) {
            case EVENT_DATE -> PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
            case VIEWS -> PageRequest.of(from / size, size, Sort.by("views").descending());
            case null -> PageRequest.of(from / size, size);
        };

        List<Event> events = eventRepository.findPublicEvents(
                textPattern,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                Boolean.TRUE.equals(onlyAvailable),
                pageable
        );

        events.forEach(this::hydrateConfirmedRequests);

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @Transactional
    public EventFullDto getPublishedEvent(Long eventId) {
        Event event = getEventById(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new ObjectNotFoundException("Event with id %d not found".formatted(eventId));
        }

        hydrateConfirmedRequests(event);

        LocalDateTime start = event.getCreatedOn() != null
                ? event.getCreatedOn()
                : event.getEventDate();
        LocalDateTime end = LocalDateTime.now();

        String uri = "/events/" + eventId;

        long views = statsClient.getViews(start, end, uri);
        event.setViews(views);
        Event saved = eventRepository.save(event);
        return eventMapper.toEventFullDto(saved);
    }

    private void hydrateConfirmedRequests(Event event) {
        long confirmed = requestRepository.countByEventIdAndStatus(
                event.getId(),
                ParticipationRequestStatus.CONFIRMED
        );
        event.setConfirmedRequests(confirmed);
    }

}
