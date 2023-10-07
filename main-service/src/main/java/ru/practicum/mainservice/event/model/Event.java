package ru.practicum.mainservice.event.model;

import lombok.*;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.event.states.State;
import ru.practicum.mainservice.location.model.Location;
import ru.practicum.mainservice.user.model.User;
import javax.persistence.*;
import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "events", schema = "public")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;

    @Column(name = "description", nullable = false, length = 7000)
    private String description;

    @Column(name = "state", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "is_paid", nullable = false)
    private Boolean paid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    @ToString.Exclude
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User initiator;

    @Column(name = "created_date", nullable = false)
    @Future
    private LocalDateTime createdOn;

    @Column(name = "published_date")
    @Future
    private LocalDateTime publishedOn;

    @Column(name = "event_date", nullable = false)
    @Future
    private LocalDateTime eventDate;

    @Column(name = "limit", nullable = false)
    private Integer participantLimit;

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    @Column(name = "confirmed_requests", nullable = false)
    private Integer confirmedRequests;

    @Column(name = "views", nullable = false)
    private Integer views;

}