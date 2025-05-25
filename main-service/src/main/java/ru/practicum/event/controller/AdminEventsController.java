package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventPatchDto;
import ru.practicum.event.dto.EventPostDto;
import ru.practicum.event.service.EventService;

@Slf4j
@RestController
@RequestMapping("/admin/events")
public class AdminEventsController {
    private final EventService eventService;
    public AdminEventsController(EventService eventService) {
        this.eventService = eventService;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent (@PathVariable("eventId") int eventId,
                                    @RequestBody EventPatchDto eventDto) {
        log.info("Patching event {}, eventId {} ", eventDto, eventId);
        EventFullDto eventFullDto = eventService.updateEvent(eventDto, eventId);
        return eventFullDto;
    }
}
