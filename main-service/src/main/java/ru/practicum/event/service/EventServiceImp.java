package ru.practicum.event.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventPatchDto;
import ru.practicum.event.dto.EventPostDto;
import ru.practicum.event.dto.SortEnum;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventParameters;
import ru.practicum.event.model.EventParametersPublic;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserParameters;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class EventServiceImp implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);


    public EventServiceImp(EventRepository eventRepository, UserRepository userRepository, EventMapper eventMapper, CategoryRepository categoryRepository, RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public EventFullDto createEvent(EventPostDto eventDto) {
        User user = userRepository.findById(eventDto.getInitiator().longValue()).orElseThrow(()-> new NotFoundException(String.format("User Not Found id: %s", eventDto.getInitiator())));
        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(()-> new NotFoundException(String.format("Category Not Found id: %s", eventDto.getCategory())));
        Event newEvent = eventMapper.toEvent(eventDto,user,category);
        newEvent.setState(EventState.PENDING);
        newEvent.setCreatedOn(LocalDateTime.now());
        checkTimeEvent(newEvent);
        if (newEvent.getParticipantLimit() < 0) {
            throw new ValidationException(String.format("Participant Limit is less than or equal to 0"));
        }

        if (newEvent.getPaid() == null) {
            newEvent.setPaid(false);
        }
        if (newEvent.getParticipantLimit() == null) {
            newEvent.setParticipantLimit(0);
        }
        if (newEvent.getRequestModeration() == null) {
            newEvent.setRequestModeration(true);
        }
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
        //User user = userRepository.findById(Long.valueOf(eventDto.getInitiator())).orElseThrow(()-> new NotFoundException(String.format("User Not Found id: %s", eventDto.getInitiator())));
        //Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(()-> new NotFoundException(String.format("Category Not Found id: %s", eventDto.getCategory())));
        //Event eventOld = eventRepository.findById(Long.valueOf(eventId)).orElseThrow(()-> new NotFoundException((String.format("Event Not Found id: %s", eventId))));
        Optional<Event> eventCheck = Optional.ofNullable(eventRepository.getEventById(eventId));
        if (eventCheck.isEmpty()) {
            throw  new NotFoundException((String.format("Event Not Found id: %s", eventId)));
        }
        Event eventOld = eventCheck.get();

        if (eventDto.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime.parse(eventDto.getEventDate(), DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
            eventOld.setEventDate(newEventDate);

            checkTimeEvent(eventOld);
        }

        //LocalDateTime nowDate = LocalDateTime.now();
        //LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(),
          //      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

//        if (eventDate.isBefore(nowDate.plusHours(1))) {
//            throw new ValidationException("For the requested operation the conditions are not met.");
//        }

//        if (!eventOld.getEventDate().equals(eventDto.getEventDate()) && eventDto.getEventDate() != null) {
//            LocalDateTime newEventDate = LocalDateTime.parse(eventDto.getEventDate(), DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
//            eventOld.setEventDate(newEventDate);
//        }

        if (eventOld.getState().equals("PUBLISH")) {
            throw new ValidationException("For the requested operation the conditions are not met.");
        }

        if (eventOld.getPaid() != eventDto.getPaid() && eventDto.getPaid() != null) {
            eventOld.setPaid(eventDto.getPaid());
        }
        if (eventOld.getRequestModeration() != eventDto.getRequestModeration() && eventDto.getRequestModeration() != null) {
            eventOld.setRequestModeration(eventDto.getRequestModeration());
        }

        if (eventDto.getLocation() != null) {
            eventOld.setLocationLatitude(eventDto.getLocation().getLat());
        }
        if (eventDto.getLocation() != null) {
            eventOld.setLocationLongitude(eventDto.getLocation().getLon());
        }
        if (eventOld.getDescription() != eventDto.getDescription() && eventDto.getDescription() != null) {
            eventOld.setDescription(eventDto.getDescription());
        }
        if (eventOld.getAnnotation() != eventDto.getAnnotation() && eventDto.getAnnotation() != null) {
            eventOld.setAnnotation(eventDto.getAnnotation());
        }
        if (eventOld.getParticipantLimit() != eventDto.getParticipantLimit() && eventDto.getParticipantLimit() != null) {
            eventOld.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventOld.getTitle() != eventDto.getTitle() && eventDto.getTitle() != null) {
            eventOld.setTitle(eventDto.getTitle());
        }

        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case "SEND_TO_REVIEW":
                    eventOld.setState(EventState.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    eventOld.setState(EventState.CANCELED);
                    break;
                case "PUBLISH_EVENT":
                    eventOld.setState(EventState.PUBLISHED);
                    eventOld.setPublishedOn(LocalDateTime.now());
                    break;
            }
        }
        eventOld = eventRepository.save(eventOld);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventOld);
        return eventFullDto;
    }


    @Override
    public EventFullDto updateEventPrivate(Long userId, EventPatchDto eventPatchDto, Long eventId) {
        Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));
        Long initiatorId = Long.valueOf(oldEvent.getInitiator().getId());
        if (!initiatorId.equals(userId)) {
            throw new ValidationException("Unable to retrieve full event information.");
        }
        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Only canceled events can be modified.");
        }
        if (eventPatchDto.getEventDate() != null) {
            oldEvent.setEventDate(LocalDateTime.parse(eventPatchDto.getEventDate(), FORMATTER));
            checkTimeEvent(oldEvent);
        }
        if (eventPatchDto.getStateAction() != null) {
            switch (eventPatchDto.getStateAction()) {
                case "SEND_TO_REVIEW":
                    oldEvent.setState(EventState.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    oldEvent.setState(EventState.CANCELED);
                    break;
            }
        }
        EventFullDto eventFullDto = eventMapper.toEventFullDto(oldEvent);
        return eventFullDto;
    }

    @Override
    public List<EventFullDto> getEventsWithParameters (EventParameters eventParameters) {
        checkStartEnd(eventParameters.getRangeStart(), eventParameters.getRangeEnd());

        //Page<Event> events = eventRepository.getEvents(PageRequest.of(eventParameters.getFrom() / eventParameters.getSize(), eventParameters.getSize()), eventParameters.getUsers(), eventParameters.getStates(), eventParameters.getCategories(), eventParameters.getRangeStart(), eventParameters.getRangeEnd());
        Page<Event> events = eventRepository.getEvents(PageRequest.of(eventParameters.getFrom() / eventParameters.getSize(), eventParameters.getSize()), eventParameters.getUsers(), eventParameters.getStates(), eventParameters.getCategories());
       /* Page<Event> events = eventRepository
                .getEvents(PageRequest.of(eventParameters.getFrom() / eventParameters.getSize(), eventParameters.getSize()),
                        eventParameters.getUsers(),
                        eventParameters.getStates(),
                        eventParameters.getCategories()
                );

        */
        Stream<Event> eventStream = events.stream();
        if (eventParameters.getRangeStart() != null) {
            eventStream = eventStream.filter(e -> !e.getEventDate().isBefore(eventParameters.getRangeStart()));
        }
        if (eventParameters.getRangeEnd() != null) {
            eventStream = eventStream.filter(e -> !e.getEventDate().isAfter(eventParameters.getRangeEnd()));
        }

        List<EventFullDto> eventFullDtos = eventStream.map(eventMapper::toEventFullDto).collect(toList());
        return eventFullDtos;

    }

    @Override
    public List<EventFullDto> getEventsPublic (EventParametersPublic eventParameters) {
        checkStartEnd(eventParameters.getRangeStart(), eventParameters.getRangeEnd());
        List<Event> events = new ArrayList<>(eventRepository.searchEvent(eventParameters.getText(),
                eventParameters.getCategories(),
                eventParameters.getPaid(),
                //eventParameters.getRangeStart(),
                //eventParameters.getRangeEnd(),
                eventParameters.getOnlyAvailable(),
                PageRequest.of(eventParameters.getFrom() / eventParameters.getSize(), eventParameters.getSize())));

        Stream<Event> eventStream = events.stream();
        if (eventParameters.getRangeStart() != null) {
            eventStream = eventStream.filter(e -> !e.getEventDate().isBefore(eventParameters.getRangeStart()));
        }
        if (eventParameters.getRangeEnd() != null) {
            eventStream = eventStream.filter(e -> !e.getEventDate().isAfter(eventParameters.getRangeEnd()));
        }


        List<EventFullDto> eventDtos = eventStream
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());



//
//        Map<Long, Long> views = defaultStatClientService.getEventsView(events);
        Map<Long, Long> confrimeds = getConfirmedRequests(events);
//        eventDtos.forEach(eventDtoOutShort -> {
//            eventDtoOutShort.setViews(views.getOrDefault(eventDtoOutShort.getId(), 0L));
//            eventDtoOutShort.setConfirmedRequests(confrimeds.getOrDefault(eventDtoOutShort.getId(), 0L));
//        }

        eventDtos.forEach(eventDtoOutShort -> {
//            eventDtoOutShort.setViews(views.getOrDefault(eventDtoOutShort.getId(), 0L));
            eventDtoOutShort.setConfirmedRequests(Math.toIntExact(confrimeds.getOrDefault(eventDtoOutShort.getId(), 0L)));
        });

        if (eventParameters.getSort() != null) {
            switch (SortEnum.valueOf(eventParameters.getSort())) {
                case EVENT_DATE:
                    eventDtos = eventDtos.stream().sorted(Comparator.comparing(EventFullDto::getEventDate)).collect(Collectors.toList());
                    break;
                case VIEWS:
                    eventDtos = eventDtos.stream().sorted(Comparator.comparing(EventFullDto::getViews)).collect(Collectors.toList());
                    break;
            }
        }

        return  eventDtos; //eventDtos;

    }

    @Override
    public EventFullDto getEventPublic(Long eventId) {
//        defaultStatClientService.createHit(request);
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format("Not Found event with id: {}", eventId));
        }
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
//        Map<Long, Long> views = defaultStatClientService.getEventsView(List.of(event));
//        Map<Long, Long> confrimeds = getConfirmedRequests(List.of(event));
//        eventDtoOutFull.setViews(views.getOrDefault(eventId, 0L));
//        eventDtoOutFull.setConfirmedRequests(confrimeds.getOrDefault(eventId, 0L));
        return eventFullDto;
    }

    private void checkStartEnd(LocalDateTime start, LocalDateTime end) {
        if (end != null && start != null) {
            if (end.isBefore(start)) {
                throw new ValidationException("Incorrectly made request.");
            }
        }
    }

    private void checkTimeEvent(Event event) {
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(1);
        if (event.getEventDate().isBefore(localDateTime)) {
            throw new ValidationException("For the requested operation the conditions are not met.");
        }
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> eventsId) {
        List<Event> perEvents = eventsId.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());

        List<Integer> confirmedRequestIds = perEvents.stream()
                .map(event -> event.getId().intValue())
                .collect(Collectors.toList());


        Map<Long, Long> requestStats = new HashMap<>();

        List<Long> confirmedRequestIdsAsLong = confirmedRequestIds.stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());

        if (!confirmedRequestIds.isEmpty()) {
            requestRepository.findConfirmedRequests(confirmedRequestIdsAsLong)
                    .forEach(stat -> requestStats.put(stat.getEventId().longValue(), stat.getConfirmedRequests()));
        }

        return requestStats;
    }

}
