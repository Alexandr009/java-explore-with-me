package ru.practicum.event.service;

import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventPatchDto;
import ru.practicum.event.dto.EventPostDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserParameters;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class EventServiceImp implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);


    public EventServiceImp(EventRepository eventRepository, UserRepository userRepository, EventMapper eventMapper, CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public EventFullDto createEvent(EventPostDto eventDto) {
        User user = userRepository.findById(eventDto.getInitiator().longValue()).orElseThrow(()-> new NotFoundException(String.format("User Not Found id: %s", eventDto.getInitiator())));
        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(()-> new NotFoundException(String.format("Category Not Found id: %s", eventDto.getCategory())));
        Event newEvent = eventMapper.toEvent(eventDto,user,category);
        newEvent.setState(EventState.PENDING);
        //newEvent.setCreatedOn();
        //newEvent.setPublishedOn();
        newEvent = eventRepository.save(newEvent);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(newEvent);
        return eventFullDto;
    }

    @Override
    public List<EventFullDto> getAllEvents(UserParameters userParameters) {
        List<Event> eventList = eventRepository.findAllByInitiator_Id(userParameters.getIds().getFirst());
        eventList = eventList.stream()
                .skip(userParameters.getFrom())
                .limit(userParameters.getSize())
                .collect(Collectors.toList());
        List<EventFullDto> eventFullDtos = eventList.stream().map(eventMapper::toEventFullDto).collect(toList());
        return eventFullDtos;
    }

    @Override
    public EventFullDto getEvent(Integer eventId, Integer userId) {
        Event event = eventRepository.getEventByIdAndInitiator_Id(eventId, userId);
        if (event == null) {
            throw new NotFoundException(String.format("Event Not Found id: %d", eventId));
        }
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        return eventFullDto;
    }

    @Override
    public EventFullDto updateEvent(EventPatchDto eventDto, Integer eventId) {
        User user = userRepository.findById(eventDto.getInitiator().longValue()).orElseThrow(()-> new NotFoundException(String.format("User Not Found id: %s", eventDto.getInitiator())));
        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(()-> new NotFoundException(String.format("Category Not Found id: %s", eventDto.getCategory())));
        LocalDateTime nowDate = LocalDateTime.now();
        LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (eventDate.isBefore(nowDate.plusHours(1))) {
            throw new ValidationException("For the requested operation the conditions are not met.");
        }
        if (eventDto.getStateAction().equals("PUBLISH_EVENT")) {
            throw new ValidationException("For the requested operation the conditions are not met.");
        }

        Event newEvent = eventMapper.fromEventPatchtoEvent(eventDto,user,category);
        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case "SEND_TO_REVIEW":
                    newEvent.setState(EventState.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    newEvent.setState(EventState.CANCELED);
                    break;
            }
        }
        newEvent.setId(eventId);
        newEvent = eventRepository.save(newEvent);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(newEvent);
        return eventFullDto;
    }

}
