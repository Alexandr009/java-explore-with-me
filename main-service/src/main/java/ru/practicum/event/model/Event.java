package ru.practicum.event.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String annotation;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    Category category;
    @ManyToOne(fetch = FetchType.EAGER)
            @JoinColumn(name = "user_id")
    User initiator;
    String description;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @Column(name = "created_on")
    LocalDateTime createdOn;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "location_latitude")
    Double locationLatitude;
    @Column(name = "location_longitude")
    Double locationLongitude;
    Boolean paid;
    @Column(name = "participant_limit")
    String participantLimit;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    String title;
    EventState state;
}
