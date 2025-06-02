package ru.practicum.complication.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Event;

import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "complication")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Complication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "title")
    String title;
    @Column(name = "is_pinned")
    Boolean pinned;
    @ManyToMany
    @JoinTable(name = "complication_events",
            joinColumns = {@JoinColumn(name = "complication_id")},
            inverseJoinColumns = {@JoinColumn(name = "events_id")})
    List<Event> events;
}
