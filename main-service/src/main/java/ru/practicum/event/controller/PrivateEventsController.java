package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventPostDto;
import ru.practicum.event.service.EventService;
import ru.practicum.user.model.UserParameters;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/")
public class PrivateEventsController {
    private final EventService eventService;
    public PrivateEventsController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable long userId, @RequestBody EventPostDto eventDto) {
        log.info("Creating event for user {} with event {}", userId, eventDto);
        eventDto.setInitiator((int)userId);
        EventFullDto eventNew = eventService.createEvent(eventDto);
        return eventNew;
    }

    @GetMapping("/{userId}/events")
    public List<EventFullDto> getEvents(@PathVariable long userId,
                                        @RequestParam(required = false) Integer from,
                                        @RequestParam(required = false) Integer size) {
        log.info("Getting events for user {} from {} size {}", userId, from, size);

        UserParameters userParameters = new UserParameters();
        userParameters.setIds(List.of((int) userId));
        userParameters.setFrom(from);
        userParameters.setSize(size);
        List<EventFullDto> events = eventService.getAllEvents(userParameters);
        return events;
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEvent(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Getting event for user {} with event {}", userId, eventId);
        EventFullDto eventFullDto = eventService.getEvent((int)eventId, (int)userId);
        return eventFullDto;
    }

}
