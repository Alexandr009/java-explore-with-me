package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator_Id(Integer initiatorId);

    Event getEventByIdAndInitiator_Id(Integer id, Integer initiatorId);

    Event getEventById(Integer id);
}
