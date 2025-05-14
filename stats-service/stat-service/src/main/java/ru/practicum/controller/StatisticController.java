package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatisticDto;
import ru.practicum.service.StatisticServiceImp;

import java.util.List;

@RestController
@Slf4j
public class StatisticController {

    @Autowired
    public final StatisticServiceImp statisticService;


    public StatisticController(StatisticServiceImp statisticService) {
        this.statisticService = statisticService;
    }

    @PostMapping("/hit")
    public StatisticDto createHit(@RequestBody StatisticDto statisticDto) {
        log.info(String.format("Creating hit statistic statisticObject - %s", statisticDto.toString()));
        return statisticService.saveStatistic(statisticDto);
    }

    @GetMapping("/stats")
    public List<StatisticDto> getStatistic(@RequestParam("start") String start,
                                     @RequestParam("end") String end,
                                     @RequestParam(value = "uris", required = false) List<String> uris,
                                     @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        log.info(String.format("Getting statistics start - %s, end - %s, uris - %s, unique - %s", start, end, uris, unique));
        return statisticService.getStatistic(start, end, uris, unique);
    }

}
