package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventPatchDto;
import ru.practicum.event.dto.EventPostDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventParameters;
import ru.practicum.event.model.EventParametersPublic;
import ru.practicum.user.model.UserParameters;

import java.util.List;

public interface EventService {
    EventFullDto createEvent(EventPostDto eventDto);
    List<EventFullDto> getAllEvents(UserParameters userParameters);
    EventFullDto getEvent(Integer eventId, Integer userId);
    EventFullDto updateEvent(EventPatchDto eventDto, Integer eventId);
    List<EventFullDto> getEventsWithParameters(EventParameters eventParameters);
    List<EventFullDto> getEventsPublic(EventParametersPublic eventParameters, HttpServletRequest request);
    EventFullDto getEventPublic(Long eventId, HttpServletRequest request);
    EventFullDto updateEventPrivate(Long userId, EventPatchDto eventDto, Long eventId);
}
