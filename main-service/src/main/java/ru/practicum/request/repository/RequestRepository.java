package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.dto.StatsConfirmed;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.StatusRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Request findByRequester_IdAndEvent_Id(Long userId, Long eventId);
    List<Request> findAllByEvent_Initiator_IdAndEvent_Id(long userId, long eventId);

    @Query("SELECT new ru.practicum.event.dto.StatsConfirmed(pr.event.id, COUNT(pr.id)) " +
            "FROM Request pr " +
            "WHERE pr.event.id IN ?1 AND pr.status = 'CONFIRMED' " +
            "GROUP BY pr.event.id")
    List<StatsConfirmed> findConfirmedRequests(List<Long> eventIds);

    Long countAllByEventIdAndStatus(Long eventId, StatusRequest statusRequest);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.event.id = :eventId AND r.status = :status")
    long countByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") String status);
}