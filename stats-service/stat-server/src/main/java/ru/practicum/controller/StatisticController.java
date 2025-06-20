package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatisticDto;
import ru.practicum.dto.StatisticInfoDto;
import ru.practicum.service.StatisticServiceImp;

import java.util.List;

@RestController
@Slf4j
@Validated
public class StatisticController {

    @Autowired
    public final StatisticServiceImp statisticService;

    public StatisticController(StatisticServiceImp statisticService) {
        this.statisticService = statisticService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatisticDto createHit(@RequestBody StatisticDto statisticDto) {
        log.info("Creating hit statistic statisticObject - {}", statisticDto.toString());
        return statisticService.saveStatistic(statisticDto);
    }

    @GetMapping("/stats")
    public List<StatisticInfoDto> getStatistic(@RequestParam("start") String start,
                                               @RequestParam("end") String end,
                                               @RequestParam(value = "uris", required = false) List<String> uris,
                                               @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        log.info("Getting statistics start - {}, end - {}, uris - {}, unique - {}", start, end, uris, unique);
        return statisticService.getStatistic(start, end, uris, unique);
    }
}