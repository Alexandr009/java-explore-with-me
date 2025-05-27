package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventPatchDto {
    String annotation;
    Integer category;
    Integer initiator;
    String description;
    String eventDate;
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    String title;
    String stateAction;
}
