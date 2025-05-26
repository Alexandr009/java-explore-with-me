package ru.practicum.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventPatchDto;
import ru.practicum.event.dto.EventPostDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EventMapper {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    public Event toEvent (EventPostDto eventPostDto, User user, Category category) {
        Event event = new Event();
        event.setEventDate(LocalDateTime.parse(eventPostDto.getEventDate(), DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        event.setPublishedOn(LocalDateTime.now());
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription(eventPostDto.getDescription());
        event.setAnnotation(eventPostDto.getAnnotation());
        event.setLocationLatitude(eventPostDto.getLocation().getLat());
        event.setLocationLongitude(eventPostDto.getLocation().getLon());
        event.setPaid(eventPostDto.getPaid());
        event.setParticipantLimit(eventPostDto.getParticipantLimit());
        event.setRequestModeration(eventPostDto.getRequestModeration());
        event.setCategory(category);
        event.setInitiator(user);
        event.setTitle(eventPostDto.getTitle());
        return event;
    }

    public EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setCategory(event.getCategory());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setConfirmedRequests(1);
        eventFullDto.setInitiator(event.getInitiator());
        eventFullDto.setState(event.getState());
        eventFullDto.setLocation(new Location(event.getLocationLatitude(),event.getLocationLongitude()));
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        return eventFullDto;

    }

    public Event fromEventPatchtoEvent (EventPatchDto eventPatchDto, User user, Category category) {
        Event event = new Event();
        event.setEventDate(LocalDateTime.parse(eventPatchDto.getEventDate(), DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        event.setPublishedOn(LocalDateTime.now());
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription(eventPatchDto.getDescription());
        event.setAnnotation(eventPatchDto.getAnnotation());
        event.setLocationLatitude(eventPatchDto.getLocation().getLat());
        event.setLocationLongitude(eventPatchDto.getLocation().getLon());
        event.setPaid(eventPatchDto.getPaid());
        event.setParticipantLimit(eventPatchDto.getParticipantLimit());
        event.setRequestModeration(eventPatchDto.getRequestModeration());
        event.setCategory(category);
        event.setInitiator(user);
        event.setTitle(eventPatchDto.getTitle());
        return event;
    }

}
