package ru.practicum.complication.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.dto.EventFullDto;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComplicationDtoOut {
    Long id;
    String title;
    List<EventFullDto> events;
    Boolean pinned;
}
