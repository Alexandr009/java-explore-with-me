package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Data
public class EventFullDto {
    Integer id;
    String annotation;
    Category category;
    User initiator;
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
    LocalDateTime publishedOn;
    Location location;
    Boolean paid;
    String participantLimit;
    Boolean requestModeration;
    String title;
    EventState state;
    Integer views;
    Integer confirmedRequests;
}
