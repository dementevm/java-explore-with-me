package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCategory(Category category);

    @Query(value = """
            SELECT * FROM events e
            WHERE e.initiator_id = :userId
            LIMIT :size OFFSET :from
            """, nativeQuery = true)
    List<Event> findByUserIdWithOffsetAndLimit(
            @Param("userId") long userId,
            @Param("from") int from,
            @Param("size") int size
    );

    @Query("""
            SELECT e
            FROM Event e
            WHERE (:users IS NULL OR e.initiator.id IN :users)
              AND (:states IS NULL OR e.state IN :states)
              AND (:categories IS NULL OR e.category.id IN :categories)
              AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart)
              AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)
            """)
    List<Event> findAdminEvents(
            @Param("users") List<Long> users,
            @Param("states") List<EventState> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );

    @Query("""
            SELECT e
            FROM Event e
            WHERE e.state = ru.practicum.ewm.event.enums.EventState.PUBLISHED
              AND (:text IS NULL OR (
                    LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))
                    OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))
              ))
              AND (:categories IS NULL OR e.category.id IN :categories)
              AND (:paid IS NULL OR e.paid = :paid)
              AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart)
              AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)
              AND (:onlyAvailable = false
                   OR e.participantLimit = 0
                   OR e.confirmedRequests < e.participantLimit)
            """)
    List<Event> findPublicEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable
    );
}
