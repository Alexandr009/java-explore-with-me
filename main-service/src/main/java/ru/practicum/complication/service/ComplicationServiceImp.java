package ru.practicum.complication.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.complication.dto.ComplicationDtoIn;
import ru.practicum.complication.dto.ComplicationDtoOut;
import ru.practicum.complication.dto.UpdateComplicationDtoIn;
import ru.practicum.complication.mapper.ComplicationMapper;
import ru.practicum.complication.model.Complication;
import ru.practicum.complication.repository.ComplicationRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ComplicationServiceImp implements ComplicationService {
    private final ComplicationRepository complicationRepository;
    private final EventRepository eventsRepository;
    private final ComplicationMapper complicationMapper;
    private final EventMapper eventMapper;
    //private final DefaultStatClientService defaultStatClientService;

    public ComplicationServiceImp(ComplicationRepository complicationRepository, EventRepository eventsRepository) {
        this.complicationRepository = complicationRepository;
        this.eventsRepository = eventsRepository;
        this.complicationMapper = new ComplicationMapper();
        this.eventMapper = new EventMapper();
    }

    @Override
    public ComplicationDtoOut create(ComplicationDtoIn complicationDtoIn) {
        List<Event> events = eventsRepository.findAllById(complicationDtoIn.getEvents() == null ? Collections.emptyList() : complicationDtoIn.getEvents());
        if (complicationDtoIn.getPinned() == null) {
            complicationDtoIn.setPinned(false);
        }
        Complication complication = complicationRepository.save(ComplicationMapper.toEntity(complicationDtoIn, events));
        return setComplicationViews(events, complication);
    }

    @Override
    public void deleteComplication(Long complicationId) {
        Complication complication = complicationRepository.findById(complicationId)
                .orElseThrow(() -> new NotFoundException(String.format("Complication id = %d not found", complicationId)));
        complicationRepository.delete(complication);

    }

    @Override
    public ComplicationDtoOut updateComplication(Long complicationId, UpdateComplicationDtoIn complicationDtoIn) {
        Complication oldComplication = complicationRepository.findById(complicationId)
                .orElseThrow(() -> new NotFoundException(String.format("Complication id = %d not found", complicationId)));
        if (complicationDtoIn.getEvents() != null) {
            oldComplication.setEvents(eventsRepository.findAllById(complicationDtoIn.getEvents()));
        }
        complicationRepository.save(oldComplication);
        return setComplicationViews(oldComplication.getEvents(), oldComplication);
    }

    @Override
    public List<ComplicationDtoOut> getComplications(Boolean pinned, Integer from, Integer size) {
        List<ComplicationDtoOut> complicationDtoOuts;
        if (pinned == null) {
            complicationDtoOuts = complicationRepository
                    .findAll(PageRequest.of(from / size, size))
                    .stream()
                    .map(complic -> setComplicationViews(complic.getEvents(), complic))
                    .collect(Collectors.toList());
            return complicationDtoOuts;

        }
        complicationDtoOuts =  complicationRepository
                .findAllByPinned(pinned, PageRequest.of(from / size, size))
                .stream()
                .map(complic -> setComplicationViews(complic.getEvents(), complic))
                .collect(Collectors.toList());
        return complicationDtoOuts;
    }

    @Override
    public ComplicationDtoOut getComplication(Long complicationId) {
        Complication complication = complicationRepository.findById(complicationId)
                .orElseThrow(() -> new NotFoundException(String.format("Complication id = %d not found", complicationId)));
        ComplicationDtoOut complicationDtoOut = setComplicationViews(complication.getEvents(), complication);
        return complicationDtoOut;
    }

    private ComplicationDtoOut setComplicationViews(List<Event> events, Complication complication) {
        List<EventFullDto> eventFills = new ArrayList<>();

;
//        if (!events.isEmpty()) {
//            Map<Long, Long> views = defaultStatClientService.getEventsView(events);
//            eventFills = events.stream()
//                    .map(eventMapper::toEventFullDto)
//                    .collect(Collectors.toList());
//            eventFills.forEach(
//                    eventDtoOutFull -> {
//                        eventDtoOutFull.se    tViews(views.getOrDefault(eventDtoOutFull.getId(), 0L));
//                    }
//            );
//        }
        eventFills = events.stream().map(eventMapper::toEventFullDto).collect(Collectors.toList());
        ComplicationDtoOut complicationDtoOut = complicationMapper.complicationDtoOut(complication, eventFills);
        return complicationDtoOut;
    }
}
