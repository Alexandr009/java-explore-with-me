package ru.practicum.service;

import org.springframework.stereotype.Service;
import ru.practicum.dto.StatisticDto;
import ru.practicum.dto.StatisticInfoDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.Mapper;
import ru.practicum.model.Statistic;
import ru.practicum.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticServiceImp implements StatisticService {
    private final StatisticRepository statisticRepository;
    private final Mapper mapper;
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public StatisticServiceImp(StatisticRepository statisticRepository, Mapper mapper) {
        this.statisticRepository = statisticRepository;
        this.mapper = mapper;
    }

    @Override
    public StatisticDto saveStatistic(StatisticDto statisticDto) {
        Statistic statistic = statisticRepository.save(mapper.mapStatistic(statisticDto));
        return mapper.mapStatisticDto(statistic);
    }

    @Override
    public List<StatisticInfoDto> getStatistic(String start, String end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

        LocalDateTime startDate;
        LocalDateTime endDate;

        try {
            startDate = LocalDateTime.parse(start, formatter);
            endDate = LocalDateTime.parse(end, formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format. Expected format: yyyy-MM-dd HH:mm:ss");
        }

        if (endDate.isBefore(startDate)) {
            throw new ValidationException("End date cannot be before start date");
        }

        List<StatisticInfoDto> listStatisticInfoDto;

        if (unique) {
            listStatisticInfoDto = statisticRepository.findByTimestampBetweenDistinct(startDate, endDate);
        } else {
            listStatisticInfoDto = statisticRepository.findByTimestampBetween(startDate, endDate);
        }

        if (uris != null && !uris.isEmpty()) {
            listStatisticInfoDto = listStatisticInfoDto.stream()
                    .filter(item -> uris.contains(item.getUri()))
                    .collect(Collectors.toList());
        }

        listStatisticInfoDto = listStatisticInfoDto.stream()
                .sorted((o1, o2) -> o2.getHits().compareTo(o1.getHits()))
                .collect(Collectors.toList());

        return listStatisticInfoDto;
    }
}