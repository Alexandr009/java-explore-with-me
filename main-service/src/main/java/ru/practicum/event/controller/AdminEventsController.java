package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventPatchDto;
import ru.practicum.event.dto.EventPostDto;
import ru.practicum.event.model.EventParameters;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@Validated
public class AdminEventsController {
    @Autowired
    private final EventService eventService;
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public AdminEventsController(EventService eventService) {
        this.eventService = eventService;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent (@PathVariable("eventId") int eventId,
                                    @Valid @RequestBody EventPatchDto eventDto) {
        log.info("Patching event {}, eventId {} ", eventDto, eventId);
        EventFullDto eventFullDto = eventService.updateEvent(eventDto, eventId);
        return eventFullDto;
    }

    @GetMapping
    public List<EventFullDto> getAllEvents(@RequestParam(required = false) List<Long> users,
                                           @RequestParam(required = false) List<EventState> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                           @RequestParam(value = "from", defaultValue = "0") Integer from,
                                           @RequestParam(value = "size", defaultValue = "10") Integer size) {


        EventParameters eventParameters = new EventParameters();
        //eventParameters.setUsers(users != null ? users.stream().map(Long::intValue).collect(Collectors.toList()) : null);
        eventParameters.setStates(states);
        //eventParameters.setCategories(categories != null ? categories.stream().map(Long::intValue).collect(Collectors.toList()) : null);
        eventParameters.setRangeStart(rangeStart);
        eventParameters.setRangeEnd(rangeEnd);
        eventParameters.setFrom(from);
        eventParameters.setSize(size);
        if (users != null && users.size() == 1 && users.get(0) == 0L) {
            eventParameters.setUsers(null);
        } else {
            eventParameters.setUsers(users != null ? users.stream().map(Long::intValue).collect(Collectors.toList()) : null);
        }
        if (categories != null && categories.size() == 1 && categories.get(0) == 0L) {
            eventParameters.setCategories(null);
        } else {
            eventParameters.setCategories(categories != null ? categories.stream().map(Long::intValue).collect(Collectors.toList()) : null);
        }
        log.info("Getting all the events for {}", eventParameters);
        List<EventFullDto> eventFullDtoList = eventService.getEventsWithParameters(eventParameters);
        return eventFullDtoList;
    }
}
