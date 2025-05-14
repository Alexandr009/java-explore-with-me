package ru.practicum.service;

import ru.practicum.dto.StatisticDto;

import java.util.List;

public interface StatisticService {
    StatisticDto saveStatistic (StatisticDto statisticDto);
    List<StatisticDto> getStatistic(String start, String end, List<String> uris, Boolean unique);
}
