package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.enums.ParticipationRequestStatus;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdInAndEventId(Iterable<Long> ids, Long eventId);

    List<Request> findAllByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

    long countByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

    List<Request> findByRequesterId(Long requesterId);

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);
}
