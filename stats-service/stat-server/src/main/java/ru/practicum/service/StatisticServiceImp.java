package ru.practicum.service;

import org.springframework.stereotype.Service;
import ru.practicum.dto.StatisticDto;
import ru.practicum.dto.StatisticInfoDto;
import ru.practicum.mapper.Mapper;
import ru.practicum.model.Statistic;
import ru.practicum.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticServiceImp implements StatisticService {
    private final StatisticRepository statisticRepository;
    private final Mapper mapper;

    public StatisticServiceImp(StatisticRepository statisticRepository, Mapper mapper) {
        this.statisticRepository = statisticRepository;
        this.mapper = mapper;
    }

    @Override
    public StatisticDto saveStatistic (StatisticDto statisticDto) {

        Statistic statistic = statisticRepository.save(mapper.mapStatistic(statisticDto));
        return mapper.mapStatisticDto(statistic);
    }

    @Override
    public List<StatisticInfoDto> getStatistic(String start, String end, List<String> uris, Boolean unique) {
        if (!uris.isEmpty()){
            //List<StatisticDto> list = new ArrayList<>();
            LocalDateTime startDate = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endDate = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            long tt = statisticRepository.countByTimestampBetween(startDate, endDate);

            List<StatisticInfoDto> listStatisticInfoDto =  statisticRepository.findByTimestampBetween(startDate, endDate);
            return listStatisticInfoDto;

/*
            List<Statistic> listAll = statisticRepository.getByTimestampBetween();//(startDate, endDate);
            System.out.println("listAll - " +  listAll.size());

            List<Statistic> list1 = statisticRepository.findAll();
            System.out.println(list1.size());
            //return list1.stream().map(mapper::mapStatisticDto).collect(Collectors.toList());

 */
        }

        return null;
    }


}
