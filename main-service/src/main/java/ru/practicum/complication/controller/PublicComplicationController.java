package ru.practicum.complication.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.complication.dto.ComplicationDtoOut;
import ru.practicum.complication.service.ComplicationServiceImp;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
public class PublicComplicationController {

    private final ComplicationServiceImp complicationService;

    public PublicComplicationController(ComplicationServiceImp complicationService) {
        this.complicationService = complicationService;
    }

    @GetMapping("/{compId}")
    public ComplicationDtoOut getComplicationById(@PathVariable("compId") Long id) {
        log.info("Get complication by id: " + id);
        ComplicationDtoOut complicationDtoOut = (complicationService.getComplication(id));
        return complicationDtoOut;
    }

    @GetMapping
    public List<ComplicationDtoOut> getComlications(@RequestParam(required = false) boolean pinned,
                                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get complications from: " + from + " size: " + size + " pinned: " + pinned);
        List<ComplicationDtoOut> complicationDtoOuts = complicationService.getComplications(pinned, from, size);
        return complicationDtoOuts;
    }

}
