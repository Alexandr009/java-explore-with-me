package ru.practicum.request.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.request.dtro.RequestDto;
import ru.practicum.request.model.Request;

@Component
public class RequestMapper {

    public static RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .requester(Long.valueOf(request.getRequester().getId()))
                .event(Long.valueOf(request.getEvent().getId()))
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }
}
