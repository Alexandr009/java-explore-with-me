package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator_Id(Integer initiatorId);
    Event getEventByIdAndInitiator_Id(Integer id, Integer initiatorId);
    Event getEventById(Integer id);
/*
    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (COALESCE(:text, ' ' ) IS ' ' OR (LOWER(e.annotation) LIKE LOWER(concat('%', :text, '%')) OR LOWER(e.description) LIKE LOWER(concat('%', :text, '%')))) " +
            "AND (COALESCE(:categoryIds, NULLIF(1,1) ) IS NULL OR e.category.id IN :categoryIds) " +
            "AND (COALESCE(:paid, FALSE) IS FALSE OR e.paid = :paid) " +
            "AND (COALESCE(:rangeStart, ' ' ) IS ' ' OR e.eventDate >= :rangeStart) " +
            "AND (COALESCE(:rangeEnd, ' ' ) IS ' ' OR e.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable = false OR e.id IN " +
            "(SELECT r.event.id " +
            "FROM Request r " +
            "WHERE r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id " +
            "HAVING e.participantLimit - count(id) > 0 " +
            "ORDER BY count(r.id))) ")
    List<Event> searchEvent(@Param("text") String text,
                            @Param("categoryIds") List<Integer> categoryIds,
                            @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                            @Param("rangeEnd") LocalDateTime rangeEnd, @Param("onlyAvailable") Boolean onlyAvailable, Pageable pageable);


 */



    // Простой метод для получения всех опубликованных событий
    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED'")
    List<Event> findAllPublishedEvents(Pageable pageable);

    // Метод без пагинации для получения всех событий
    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED'")
    List<Event> findAllPublishedEventsNoPaging();

    // Метод для фильтрации по категориям
    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED' AND e.category.id IN :categoryIds")
    List<Event> findPublishedEventsByCategories(@Param("categoryIds") List<Integer> categoryIds, Pageable pageable);

    // Метод для текстового поиска
    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED' AND " +
            "(LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Event> findPublishedEventsByText(@Param("text") String text, Pageable pageable);


/// //////////////////////////////
    @Query("SELECT e FROM Event e " +
            "WHERE (:userIds IS NULL OR e.initiator.id IN :userIds) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categoryIds IS NULL OR e.category.id IN :categoryIds)")
    Page<Event> getEvents(Pageable pageable,
                          @Param("userIds") List<Integer> userIds,
                          @Param("states") List<EventState> states,
                          @Param("categoryIds") List<Integer> categoryIds);


    /*
    @Query("SELECT e FROM Event e " +
            "WHERE (:userIds IS NULL OR e.initiator.id IN :userIds) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categoryIds IS NULL OR e.category.id IN :categoryIds) " +
            //"AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)")
    Page<Event> getEvents(Pageable pageable,
                          @Param("userIds") List<Integer> userIds,
                          @Param("states") List<EventState> states,
                          @Param("categoryIds") List<Integer> categoryIds,
                          //@Param("rangeStart") LocalDateTime rangeStart,
                          @Param("rangeEnd") LocalDateTime rangeEnd);

*/
}
