package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventPatchDto;
import ru.practicum.event.dto.EventPostDto;
import ru.practicum.event.dto.EventRequestStatusUpdateDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dtro.RequestDto;
import ru.practicum.request.dtro.RequestStatusUpdateDto;
import ru.practicum.request.service.RequestServiceImp;
import ru.practicum.user.model.UserParameters;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/")
public class PrivateEventsController {
    private final EventService eventService;
    private final RequestServiceImp requestService;
    public PrivateEventsController(EventService eventService , RequestServiceImp requestService) {
        this.eventService = eventService;
        this.requestService = requestService;
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable long userId, @Valid @RequestBody EventPostDto eventDto) {
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

    @PatchMapping("{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                                       @PathVariable Long eventId,
                                                       @Valid @RequestBody EventPatchDto eventPatchDto) {
        log.info("Event with id: " + eventId + " private updated.");
        EventFullDto eventfullDto = eventService.updateEventPrivate(userId, eventPatchDto, eventId);
        return eventfullDto;
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public RequestStatusUpdateDto updateParticipationRequest(@PathVariable Long userId,
                                                             @PathVariable Long eventId,
                                                             @RequestBody EventRequestStatusUpdateDto eventRequestStatusUpdateRequestDto) {
        log.info("Patch events requests {}", eventRequestStatusUpdateRequestDto);
        RequestStatusUpdateDto requestStatusUpdateDto = requestService.updateParticipationRequest(userId, eventId, eventRequestStatusUpdateRequestDto);
        return requestStatusUpdateDto;
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getParticipationRequests(@PathVariable Long userId,
                                                                     @PathVariable Long eventId) {
        log.info("Get events requests {}", eventId);
        List<RequestDto> requestDto = requestService.getParticipationRequests(userId, eventId);
        return requestDto;
    }

}
