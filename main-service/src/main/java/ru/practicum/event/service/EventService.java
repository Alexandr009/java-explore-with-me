package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventPatchDto;
import ru.practicum.event.dto.EventPostDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventParameters;
import ru.practicum.user.model.UserParameters;

import java.util.List;

public interface EventService {
    public EventFullDto createEvent(EventPostDto eventDto);
    public List<EventFullDto> getAllEvents(UserParameters userParameters);
    public EventFullDto getEvent(Integer eventId, Integer userId);
    public EventFullDto updateEvent(EventPatchDto eventDto, Integer eventId);
    public List<EventFullDto> getEventsWithParameters (EventParameters eventParameters);
}
