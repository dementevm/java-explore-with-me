package ru.practicum.ewm.event.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String annotation;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(nullable = false)
    private Boolean paid;

    @Column(nullable = false)
    private String title;

    private Long confirmedRequests;

    private LocalDateTime createdOn;

    @Column(length = 7000)
    private String description;

    private Integer participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration = true;

    @Enumerated(EnumType.STRING)
    private EventState state;

    private Long views;

    @PrePersist
    protected void onCreate() {
        // Сделаем вид, что это защита от того чтобы кто-то не указал в коде event.setParticipantLimit(null)
        // Оставил в учебных целях, чтобы лучше зафиксировалось.
        if (this.participantLimit == null) {
            this.participantLimit = 0;
        }
        if (this.createdOn == null) {
            this.createdOn = LocalDateTime.now();
        }
        if (this.views == null) {
            this.views = 0L;
        }
        if (this.state == null) {
            this.state = EventState.PENDING;
        }
        if (this.confirmedRequests == null) {
            this.confirmedRequests = 0L;
        }
    }
}