package ru.practicum.complication.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.complication.dto.ComplicationDtoIn;
import ru.practicum.complication.dto.ComplicationDtoOut;
import ru.practicum.complication.model.Complication;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.Event;

import java.util.List;

@Component
public class ComplicationMapper {
    public static Complication toEntity(ComplicationDtoIn complicationDtoIn, List<Event> eventList) {
        return Complication.builder()
                .title(complicationDtoIn.getTitle())
                .events(eventList)
                .pinned(complicationDtoIn.getPinned())
                .build();
    }

    public static ComplicationDtoOut complicationDtoOut(Complication complication, List<EventFullDto> events) {
        return ComplicationDtoOut.builder()
                .id(complication.getId())
                .pinned(complication.getPinned())
                .title(complication.getTitle())
                .events(events)
                .build();
    }
}
