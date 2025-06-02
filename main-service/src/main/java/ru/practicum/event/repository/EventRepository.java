package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator_Id(Integer initiatorId);

    Event getEventByIdAndInitiator_Id(Integer id, Integer initiatorId);

    Event getEventById(Integer id);

    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED'")
    List<Event> findAllPublishedEvents(Pageable pageable);


    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED'")
    List<Event> findAllPublishedEventsNoPaging();

    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED' AND e.category.id IN :categoryIds")
    List<Event> findPublishedEventsByCategories(@Param("categoryIds") List<Integer> categoryIds, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED' AND " +
            "(LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Event> findPublishedEventsByText(@Param("text") String text, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (:userIds IS NULL OR e.initiator.id IN :userIds) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categoryIds IS NULL OR e.category.id IN :categoryIds)")
    Page<Event> getEvents(Pageable pageable,
                          @Param("userIds") List<Integer> userIds,
                          @Param("states") List<EventState> states,
                          @Param("categoryIds") List<Integer> categoryIds);

}
