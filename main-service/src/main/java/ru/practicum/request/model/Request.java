package ru.practicum.request.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;

    @Column(name = "created_at")
    LocalDateTime created;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    StatusRequest status;
}
