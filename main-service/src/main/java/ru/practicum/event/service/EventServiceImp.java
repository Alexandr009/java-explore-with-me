package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
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

@Slf4j
@Service
public class EventServiceImp implements EventService {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatClientService statClientService;


    public EventServiceImp(EventRepository eventRepository, UserRepository userRepository, EventMapper eventMapper, CategoryRepository categoryRepository, RequestRepository requestRepository, StatClientService statClientService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        this.statClientService = statClientService;
    }

    @Override
    public EventFullDto createEvent(EventPostDto eventDto) {
        User user = userRepository.findById(eventDto.getInitiator().longValue()).orElseThrow(() -> new NotFoundException(String.format("User Not Found id: %s", eventDto.getInitiator())));
        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(() -> new NotFoundException(String.format("Category Not Found id: %s", eventDto.getCategory())));
        Event newEvent = eventMapper.toEvent(eventDto, user, category);
        newEvent.setState(EventState.PENDING);
        newEvent.setCreatedOn(LocalDateTime.now());
        checkTimeEvent(newEvent);

        if (newEvent.getPaid() == null) {
            newEvent.setPaid(false);
        }
        if (newEvent.getParticipantLimit() == null) {
            newEvent.setParticipantLimit(0);
        }
        if (newEvent.getRequestModeration() == null) {
            newEvent.setRequestModeration(true);
        }

        if (newEvent.getParticipantLimit() < 0) {
            throw new ValidationException("Participant Limit is less than or equal to 0");
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
        Optional<Event> eventCheck = Optional.ofNullable(eventRepository.getEventById(eventId));
        if (eventCheck.isEmpty()) {
            throw new NotFoundException((String.format("Event Not Found id: %s", eventId)));
        }
        Event eventOld = eventCheck.get();

        if (eventDto.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime.parse(eventDto.getEventDate(), DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
            eventOld.setEventDate(newEventDate);

            checkTimeEvent(eventOld);
        }


        if (eventOld.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event already PUBLISHED");
        }

        if (eventOld.getState().equals(EventState.REJECT) && eventDto.getStateAction().equals("PUBLISH_EVENT")) {
            throw new ConflictException("Event already REJECTED");
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
        if (!Objects.equals(eventOld.getParticipantLimit(), eventDto.getParticipantLimit()) && eventDto.getParticipantLimit() != null) {
            eventOld.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventOld.getTitle() != eventDto.getTitle() && eventDto.getTitle() != null) {
            eventOld.setTitle(eventDto.getTitle());
        }

        if (eventDto.getStateAction() != null) {
            String status = eventDto.getStateAction();
            switch (status) {
                case "SEND_TO_REVIEW":
                    eventOld.setState(EventState.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    eventOld.setState(EventState.CANCELED);
                    break;
                case "REJECT_EVENT":
                    eventOld.setState(EventState.REJECT);
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
            throw new ConflictException("Only canceled events can be modified.");
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
    public List<EventFullDto> getEventsWithParameters(EventParameters eventParameters) {
        checkStartEnd(eventParameters.getRangeStart(), eventParameters.getRangeEnd());


        Page<Event> events = eventRepository.getEvents(PageRequest.of(eventParameters.getFrom() / eventParameters.getSize(), eventParameters.getSize()), eventParameters.getUsers(), eventParameters.getStates(), eventParameters.getCategories());

        Stream<Event> eventStream = events.stream();
        if (eventParameters.getRangeStart() != null) {
            eventStream = eventStream.filter(e -> !e.getEventDate().isBefore(eventParameters.getRangeStart()));
        }
        if (eventParameters.getRangeEnd() != null) {
            eventStream = eventStream.filter(e -> !e.getEventDate().isAfter(eventParameters.getRangeEnd()));
        }

        List<Event> filteredEvents = eventStream.collect(Collectors.toList());

        Map<Long, Long> confirmed = getConfirmedRequests(filteredEvents);

        List<EventFullDto> eventFullDtos = filteredEvents.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

        eventFullDtos.forEach(eventDto -> {
            eventDto.setConfirmedRequests(Math.toIntExact(confirmed.getOrDefault(eventDto.getId().longValue(), 0L)));
        });

        return eventFullDtos;

    }

    @Override
    public List<EventFullDto> getEventsPublic(EventParametersPublic eventParameters, HttpServletRequest request) {
        log.info("EventParametersPublic {}", eventParameters);
        checkStartEnd(eventParameters.getRangeStart(), eventParameters.getRangeEnd());

        List<Event> events = eventRepository.findAllPublishedEventsNoPaging();

        Stream<Event> eventStream = events.stream();

        if (eventParameters.getCategories() != null && !eventParameters.getCategories().isEmpty()) {
            eventStream = eventStream.filter(e ->
                    eventParameters.getCategories().contains(e.getCategory().getId())
            );
        }

        if (eventParameters.getText() != null && !eventParameters.getText().trim().isEmpty()) {
            String searchText = eventParameters.getText().toLowerCase().trim();
            eventStream = eventStream.filter(e ->
                    e.getAnnotation().toLowerCase().contains(searchText) ||
                            e.getDescription().toLowerCase().contains(searchText)
            );
        }

        if (eventParameters.getPaid() != null) {
            eventStream = eventStream.filter(e -> e.getPaid().equals(eventParameters.getPaid()));
        }

        if (eventParameters.getRangeStart() != null) {
            eventStream = eventStream.filter(e -> !e.getEventDate().isBefore(eventParameters.getRangeStart()));
        }
        if (eventParameters.getRangeEnd() != null) {
            eventStream = eventStream.filter(e -> !e.getEventDate().isAfter(eventParameters.getRangeEnd()));
        }

        if (eventParameters.getOnlyAvailable() != null && eventParameters.getOnlyAvailable()) {
            eventStream = eventStream.filter(e -> {
                if (e.getParticipantLimit() == 0) return true;
                long confirmedRequests = requestRepository.countByEventIdAndStatus(e.getId().longValue(), "CONFIRMED");
                return confirmedRequests < e.getParticipantLimit();
            });
        }

        List<Event> filteredEvents = eventStream.collect(Collectors.toList());

        if (eventParameters.getSort() != null) {
            switch (SortEnum.valueOf(eventParameters.getSort())) {
                case EVENT_DATE:
                    filteredEvents = filteredEvents.stream()
                            .sorted(Comparator.comparing(Event::getEventDate))
                            .collect(Collectors.toList());
                    break;
                case VIEWS:
                    List<EventFullDto> tempDtos = filteredEvents.stream()
                            .map(eventMapper::toEventFullDto)
                            .collect(Collectors.toList());

                    Map<Long, Long> confirmed = getConfirmedRequests(filteredEvents);
                    Map<Long, Long> views = statClientService != null ?
                            statClientService.getEventsView(filteredEvents) : new HashMap<>();

                    tempDtos.forEach(eventDto -> {
                        eventDto.setConfirmedRequests(Math.toIntExact(confirmed.getOrDefault(eventDto.getId().longValue(), 0L)));
                        eventDto.setViews(Math.toIntExact(views.getOrDefault(eventDto.getId().longValue(), 0L)));
                    });

                    tempDtos = tempDtos.stream()
                            .sorted(Comparator.comparing(EventFullDto::getViews,
                                    Comparator.nullsLast(Comparator.naturalOrder())))
                            .collect(Collectors.toList());

                    int from = eventParameters.getFrom();
                    int size = eventParameters.getSize();
                    int totalFiltered = tempDtos.size();

                    if (from >= totalFiltered) {
                        return new ArrayList<>();
                    }

                    int toIndex = Math.min(from + size, totalFiltered);
                    return tempDtos.subList(from, toIndex);
            }
        }

        int from = eventParameters.getFrom();
        int size = eventParameters.getSize();
        int totalFiltered = filteredEvents.size();

        if (from >= totalFiltered) {
            return new ArrayList<>();
        }

        int toIndex = Math.min(from + size, totalFiltered);
        List<Event> pagedEvents = filteredEvents.subList(from, toIndex);

        List<EventFullDto> eventDtos = pagedEvents.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

        Map<Long, Long> confirmed = getConfirmedRequests(pagedEvents);
        Map<Long, Long> views = statClientService != null ?
                statClientService.getEventsView(pagedEvents) : new HashMap<>();

        eventDtos.forEach(eventDto -> {
            eventDto.setConfirmedRequests(Math.toIntExact(confirmed.getOrDefault(eventDto.getId().longValue(), 0L)));
            eventDto.setViews(Math.toIntExact(views.getOrDefault(eventDto.getId().longValue(), 0L)));
        });

        return eventDtos;
    }

    @Override
    public EventFullDto getEventPublic(Long eventId, HttpServletRequest request) {
        if (statClientService != null) {
            statClientService.createHit(request);
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format("Event with id = %d not found", eventId));
        }

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);

        if (statClientService != null) {
            Map<Long, Long> views = statClientService.getEventsView(List.of(event));
            eventFullDto.setViews(Math.toIntExact(views.getOrDefault(eventId, 0L)));
        } else {
            eventFullDto.setViews(0);
        }

        Map<Long, Long> confirmed = getConfirmedRequests(List.of(event));
        eventFullDto.setConfirmedRequests(Math.toIntExact(confirmed.getOrDefault(eventId, 0L)));

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

    @Override
    public List<EventFullDto> getSubscribedUsersEvents (Long userId, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new ValidationException("From and size can't be less 0");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        List<Event> events = eventRepository.findAllByInitiatorIdAndState(userId.intValue(), EventState.PUBLISHED, PageRequest.of(from / size, size));
        List<EventFullDto> eventFullDtoList = events.stream().map(eventMapper::toEventFullDto).collect(Collectors.toList());
        return eventFullDtoList;
    }

    @Override
    public List<EventFullDto> getSubscribedAllUsersEvents(Long followerId, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new ValidationException("From and size can't be less 0");
        }
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new NotFoundException(String.format("follower id = %d not found", followerId)));

        List<User> subscribedUsers = userRepository.findUsersByFollowerContains(follower);

        if (subscribedUsers.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> userIds = subscribedUsers.stream()
                .map(user -> user.getId().longValue())
                .collect(Collectors.toList());

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiator_IdInAndStateOrderByEventDateDesc(
                userIds, EventState.PUBLISHED, pageRequest);

        Map<Long, Long> confirmed = getConfirmedRequests(events);

        List<EventFullDto> eventFullDtoList = events.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

        eventFullDtoList.forEach(eventDto -> {
            eventDto.setConfirmedRequests(Math.toIntExact(confirmed.getOrDefault(eventDto.getId().longValue(), 0L)));
        });

        return eventFullDtoList;
    }

}
