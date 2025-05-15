package ru.practicum.service;

import ru.practicum.dto.StatisticDto;
import ru.practicum.dto.StatisticInfoDto;

import java.util.List;

public interface StatisticService {
    StatisticDto saveStatistic (StatisticDto statisticDto);
    List<StatisticInfoDto> getStatistic(String start, String end, List<String> uris, Boolean unique);
}
