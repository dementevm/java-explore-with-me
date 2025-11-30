package ru.practicum.ewm.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.EventUpdateException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.RequestStatusUpdateException;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.enums.ParticipationRequestStatus;
import ru.practicum.ewm.request.enums.RequestUpdateStatus;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final EventService eventService;

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ObjectNotFoundException("Event with id %d not found".formatted(eventId));
        }
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {
        userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ObjectNotFoundException("Event with id %d not found".formatted(eventId));
        }

        List<Request> requests = requestRepository
                .findAllByIdInAndEventId(dto.requestIds(), eventId);

        if (requests.size() != dto.requestIds().size()) {
            throw new ObjectNotFoundException("One or more requests not found for event with id %d".formatted(eventId));
        }

        for (Request request : requests) {
            if (request.getStatus() != ParticipationRequestStatus.PENDING) {
                throw new RequestStatusUpdateException("Request must have status PENDING");
            }
        }

        int participantLimit = event.getParticipantLimit();
        int confirmedCount = (int) requestRepository
                .countByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);

        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        if (dto.status() == RequestUpdateStatus.CONFIRMED) {
            if (participantLimit != 0 && confirmedCount >= participantLimit) {
                throw new RequestStatusUpdateException("The participant limit has been reached");
            }
        }

        for (Request request : requests) {
            if (dto.status() == RequestUpdateStatus.CONFIRMED) {
                if (participantLimit != 0 && confirmedCount >= participantLimit) {
                    throw new RequestStatusUpdateException("The participant limit has been reached");
                }
                request.setStatus(ParticipationRequestStatus.CONFIRMED);
                confirmed.add(request);
                confirmedCount++;
            } else if (dto.status() == RequestUpdateStatus.REJECTED) {
                request.setStatus(ParticipationRequestStatus.REJECTED);
                rejected.add(request);
            }
        }

        requestRepository.saveAll(requests);

        if (dto.status() == RequestUpdateStatus.CONFIRMED
                && participantLimit != 0
                && confirmedCount >= participantLimit) {
            List<Request> pendingRequests = requestRepository
                    .findAllByEventIdAndStatus(eventId, ParticipationRequestStatus.PENDING);
            for (Request request : pendingRequests) {
                request.setStatus(ParticipationRequestStatus.REJECTED);
            }
            requestRepository.saveAll(pendingRequests);
            rejected.addAll(pendingRequests);
        }

        List<ParticipationRequestDto> confirmedDtos = confirmed.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
        List<ParticipationRequestDto> rejectedDtos = rejected.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();

        return new EventRequestStatusUpdateResult(confirmedDtos, rejectedDtos);
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        userService.getUserById(userId);
        List<Request> requests = requestRepository.findByRequesterId(userId);
        return requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Transactional
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        userService.getUserById(userId);
        User requester = userMapper.toUser(userService.getUserById(userId));

        Event event = eventService.getEventById(eventId);

        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new EventUpdateException("Initiator cannot request participation in own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new EventUpdateException("Event must be published");
        }

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new EventUpdateException("Request already exists");
        }

        long confirmed = Optional.ofNullable(event.getConfirmedRequests()).orElse(0L);
        Integer limit = event.getParticipantLimit();

        if (limit != null && limit > 0 && confirmed >= limit) {
            throw new EventUpdateException("The participant limit has been reached");
        }

        Request request = new Request();
        request.setEvent(event);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        boolean moderation = Boolean.TRUE.equals(event.getRequestModeration());
        if (!moderation || limit == null || limit == 0) {
            request.setStatus(ParticipationRequestStatus.CONFIRMED);
            event.setConfirmedRequests(confirmed + 1);
        } else {
            request.setStatus(ParticipationRequestStatus.PENDING);
        }

        Request saved = requestRepository.save(request);
        return requestMapper.toParticipationRequestDto(saved);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userService.getUserById(userId);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Request with id %d not found".formatted(requestId)));

        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new ObjectNotFoundException("Request with id %d not found".formatted(requestId));
        }

        if (request.getStatus() == ParticipationRequestStatus.CONFIRMED) {
            Event event = request.getEvent();
            long confirmed = Optional.ofNullable(event.getConfirmedRequests()).orElse(0L);
            if (confirmed > 0) {
                event.setConfirmedRequests(confirmed - 1);
            }
        }

        request.setStatus(ParticipationRequestStatus.CANCELED);
        Request saved = requestRepository.save(request);
        return requestMapper.toParticipationRequestDto(saved);
    }

}
