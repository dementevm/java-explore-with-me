package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.enums.EventSort;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@Slf4j
public class PublicEventController {
    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping("/events")
    public List<EventShortDto> getEvents(
            @RequestParam(name = "text", required = false) @Size(min = 1, max = 7000) String text,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(name = "sort", required = false) EventSort sort,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
            HttpServletRequest request
    ) {
        try {
            statsClient.hit(
                    "ewm-main-service",
                    request.getRequestURI(),
                    request.getRemoteAddr()
            );
        } catch (Exception e) {
            log.warn("Failed to send stats hit: {}", e.getMessage());
        }

        return eventService.getPublicEvents(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size
        );
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEvent(
            @PathVariable("id") @Positive Long id,
            HttpServletRequest request
    ) {
        try {
            statsClient.hit(
                    "ewm-main-service",
                    request.getRequestURI(),
                    request.getRemoteAddr()
            );
        } catch (Exception e) {
            log.warn("Failed to send stats hit: {}", e.getMessage());
        }
        return eventService.getPublishedEvent(id);
    }

}
