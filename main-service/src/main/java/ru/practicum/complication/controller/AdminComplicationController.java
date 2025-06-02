package ru.practicum.complication.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.complication.dto.ComplicationDtoIn;
import ru.practicum.complication.dto.ComplicationDtoOut;
import ru.practicum.complication.dto.UpdateComplicationDtoIn;
import ru.practicum.complication.service.ComplicationService;

@Slf4j
@RestController
@RequestMapping("/admin/compilations")
public class AdminComplicationController {
    private final ComplicationService complicationService;

    public AdminComplicationController(ComplicationService complicationService) {
        this.complicationService = complicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplicationDtoOut createComplication(@Valid @RequestBody ComplicationDtoIn complicationDtoIn) {
        log.info("Complication with title: " + complicationDtoIn.getTitle() + " saved");
        ComplicationDtoOut complicationDtoOut = complicationService.create(complicationDtoIn);
        return complicationDtoOut;
    }

    @PatchMapping("/{compId}")
    public ComplicationDtoOut updateComplication(@PathVariable("compId") Long id, @Valid @RequestBody UpdateComplicationDtoIn updateComplicationDtoIn) {
        log.info("Complication with id: " + id + " updated");
        ComplicationDtoOut complicationDtoOut = (complicationService.updateComplication(id, updateComplicationDtoIn));
        return complicationDtoOut;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComplication(@PathVariable("compId") long id) {
        log.info("Complication with id: " + id + " deleted");
        complicationService.deleteComplication(id);
    }


}
