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
    public Statistic mapStatistic(StatisticDto statistic) {
        Statistic statisticModel = new Statistic();
        statisticModel.setIp(statistic.getIp());
        statisticModel.setUri(statistic.getUri());
        statisticModel.setApp(statistic.getApp());
        statisticModel.setTimestamp(LocalDateTime.parse(statistic.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return statisticModel;
    }

    public StatisticDto mapStatisticDto(Statistic statistic) {
        StatisticDto statisticDto = new StatisticDto();
        statisticDto.setIp(statistic.getIp());
        statisticDto.setUri(statistic.getUri());
        statisticDto.setApp(statistic.getApp());
        statisticDto.setTimestamp(statistic.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return statisticDto;
    }
}
