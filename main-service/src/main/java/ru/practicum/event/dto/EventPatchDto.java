package ru.practicum.event.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.event.model.Location;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventPatchDto {
    @Size(min = 20, max = 2000, message = "Annotation length must be between 20 and 2000 characters.")
    String annotation;
    @Positive
    Integer category;
    Integer initiator;
    @Size(min = 20, max = 7000, message = "Full description length must be between 20 and 7000 characters.")
    String description;
    String eventDate;
    Location location;
    Boolean paid;
    @PositiveOrZero(message = "Participant limit cannot be negative")
    Integer participantLimit;
    Boolean requestModeration;
    @Size(min = 3, max = 120, message = "Title length must be between 3 and 120 characters.")
    String title;
    String stateAction;
}
