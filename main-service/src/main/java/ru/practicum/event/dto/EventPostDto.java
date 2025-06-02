package ru.practicum.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.event.model.Location;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventPostDto {
    @NotBlank
    @Size(min = 20, max = 2000, message = "Annotation length must be between 20 and 2000 characters.")
    String annotation;
    Integer category;
    Integer initiator;
    @NotBlank
    @Size(min = 20, max = 7000, message = "Full description length must be between 20 and 7000 characters.")
    String description;
    @NotNull
    String eventDate;
    @NotNull
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    @Size(min = 3, max = 120, message = "Title length must be between 3 and 120 characters.")
    @NotBlank
    String title;
}
