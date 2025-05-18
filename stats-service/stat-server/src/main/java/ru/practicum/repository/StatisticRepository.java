package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.StatisticInfoDto;
import ru.practicum.model.Statistic;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {
    @Query("SELECT new ru.practicum.dto.StatisticInfoDto(i.app, i.uri, COUNT(i.ip)) " +
            "FROM Statistic i " +
            "WHERE i.timestamp BETWEEN :start AND :end " +
            "GROUP BY i.app, i.uri")
    List<StatisticInfoDto> findByTimestampBetween(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatisticInfoDto(i.app, i.uri, COUNT(distinct i.ip)) " +
            "FROM Statistic i " +
            "WHERE i.timestamp BETWEEN :start AND :end " +
            "GROUP BY i.app, i.uri")
    List<StatisticInfoDto> findByTimestampBetweenDistinct(@Param("start") LocalDateTime start,
                                                          @Param("end") LocalDateTime end);
}
