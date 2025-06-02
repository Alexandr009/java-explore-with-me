package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.EventParametersPublic;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/events")
@RestController
public class PublicEventsController {
    private final EventService eventService;
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public PublicEventsController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventFullDto> getEventsPub(@RequestParam(required = false) String text,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) Boolean paid,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                           @RequestParam(required = false) String sort,
                                           @RequestParam(required = false) Integer from,
                                           @RequestParam(required = false) Integer size
    ) {
        log.info("Get public events from: " + from + " size: " + size);
        EventParametersPublic eventParametersPublic = new EventParametersPublic();
        eventParametersPublic.setText(text);
        if (categories != null && categories.size() == 1 && categories.get(0) == 0L) {
            eventParametersPublic.setCategories(null);
        } else {
            eventParametersPublic.setCategories(categories != null ? categories.stream().map(Long::intValue).collect(Collectors.toList()) : null);
        }
        eventParametersPublic.setPaid(paid != null ? paid : false);
//        eventParametersPublic.setRangeStart(rangeStart != null ? rangeStart : LocalDateTime.now());
//        eventParametersPublic.setRangeEnd(rangeEnd != null ? rangeEnd : LocalDateTime.now());
        eventParametersPublic.setRangeStart(rangeStart);
        eventParametersPublic.setRangeEnd(rangeEnd);
        eventParametersPublic.setOnlyAvailable(onlyAvailable != null ? onlyAvailable : false);
        eventParametersPublic.setSort(sort != null ? sort : null);
        eventParametersPublic.setFrom(from);
        eventParametersPublic.setSize(size);

        List<EventFullDto> events = eventService.getEventsPublic(eventParametersPublic);
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getEventPub(@PathVariable("id") Long id) {
        log.info("Get public event id: " + id);
        EventFullDto eventFullDto = eventService.getEventPublic(id);
        return eventFullDto;
    }

}
