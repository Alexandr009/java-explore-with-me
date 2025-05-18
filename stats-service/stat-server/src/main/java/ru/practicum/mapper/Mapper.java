package ru.practicum.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.StatisticDto;
import ru.practicum.model.Statistic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@Component
public class Mapper {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public Statistic mapStatistic(StatisticDto statistic) {
        Statistic statisticModel = new Statistic();
        statisticModel.setIp(statistic.getIp());
        statisticModel.setUri(statistic.getUri());
        statisticModel.setApp(statistic.getApp());
        statisticModel.setTimestamp(LocalDateTime.parse(statistic.getTimestamp(), DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        return statisticModel;
    }

    public StatisticDto mapStatisticDto(Statistic statistic) {
        StatisticDto statisticDto = new StatisticDto();
        statisticDto.setIp(statistic.getIp());
        statisticDto.setUri(statistic.getUri());
        statisticDto.setApp(statistic.getApp());
        statisticDto.setTimestamp(statistic.getTimestamp().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        return statisticDto;
    }
}
