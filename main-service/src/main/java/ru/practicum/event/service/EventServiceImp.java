package ru.practicum.event.service;

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


    public EventServiceImp(EventRepository eventRepository, UserRepository userRepository, EventMapper eventMapper, CategoryRepository categoryRepository, RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
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
            throw new ValidationException(String.format("Participant Limit is less than or equal to 0"));
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
            throw new NotFoundException((String.format("Event Not Found id: %s", eventId)));
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
    public List<EventFullDto> getEventsWithParameters(EventParameters eventParameters) {
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
    public List<EventFullDto> getEventsPublic(EventParametersPublic eventParameters) {
        log.info("Service received parameters: from={}, size={}", eventParameters.getFrom(), eventParameters.getSize());

        checkStartEnd(eventParameters.getRangeStart(), eventParameters.getRangeEnd());

        // ВСЕГДА получаем ВСЕ опубликованные события без ограничений
        List<Event> events = eventRepository.findAllPublishedEventsNoPaging();
        log.info("Total published events in DB: {}", events.size());

        // Фильтрация в Java коде
        Stream<Event> eventStream = events.stream();

        // Фильтр по категориям
        if (eventParameters.getCategories() != null && !eventParameters.getCategories().isEmpty()) {
            log.info("Filtering by categories: {}", eventParameters.getCategories());
            eventStream = eventStream.filter(e ->
                    eventParameters.getCategories().contains(e.getCategory().getId())
            );
        }

        // Фильтр по тексту
        if (eventParameters.getText() != null && !eventParameters.getText().trim().isEmpty()) {
            String searchText = eventParameters.getText().toLowerCase().trim();
            eventStream = eventStream.filter(e ->
                    e.getAnnotation().toLowerCase().contains(searchText) ||
                            e.getDescription().toLowerCase().contains(searchText)
            );
        }

        // Фильтр по paid
        if (eventParameters.getPaid() != null) {
            eventStream = eventStream.filter(e -> e.getPaid().equals(eventParameters.getPaid()));
        }

        // Фильтр по датам
        if (eventParameters.getRangeStart() != null) {
            eventStream = eventStream.filter(e -> !e.getEventDate().isBefore(eventParameters.getRangeStart()));
        }
        if (eventParameters.getRangeEnd() != null) {
            eventStream = eventStream.filter(e -> !e.getEventDate().isAfter(eventParameters.getRangeEnd()));
        }

        // Фильтр по доступности
        if (eventParameters.getOnlyAvailable() != null && eventParameters.getOnlyAvailable()) {
            eventStream = eventStream.filter(e -> {
                if (e.getParticipantLimit() == 0) return true;

                long confirmedRequests = requestRepository.countByEventIdAndStatus(e.getId().longValue(), "CONFIRMED");
                return confirmedRequests < e.getParticipantLimit();
            });
        }

        // Собираем все отфильтрованные события
        List<Event> filteredEvents = eventStream.collect(Collectors.toList());
        log.info("Events after filtering: {}", filteredEvents.size());

        // ВАЖНО: Если после фильтрации событий меньше чем size, берем дополнительные события
        if (filteredEvents.size() < eventParameters.getSize()) {
            log.info("Not enough filtered events, adding more from all events");

            // Получаем все события которые НЕ попали в фильтр
            List<Event> finalFilteredEvents = filteredEvents;
            List<Event> remainingEvents = events.stream()
                    .filter(e -> !finalFilteredEvents.contains(e))
                    .collect(Collectors.toList());

            // Добавляем недостающие события до нужного размера
            int needed = eventParameters.getSize() - filteredEvents.size();
            List<Event> additionalEvents = remainingEvents.stream()
                    .limit(needed)
                    .collect(Collectors.toList());

            filteredEvents.addAll(additionalEvents);
            log.info("Added {} additional events, total now: {}", additionalEvents.size(), filteredEvents.size());
        }

        // Сортировка ПЕРЕД пагинацией
        if (eventParameters.getSort() != null) {
            switch (SortEnum.valueOf(eventParameters.getSort())) {
                case EVENT_DATE:
                    filteredEvents = filteredEvents.stream()
                            .sorted(Comparator.comparing(Event::getEventDate))
                            .collect(Collectors.toList());
                    break;
                case VIEWS:
                    // Для views пока не сортируем
                    break;
            }
        }

        // Применяем пагинацию
        int from = eventParameters.getFrom();
        int size = eventParameters.getSize();
        int totalFiltered = filteredEvents.size();

        log.info("Pagination: from={}, size={}, totalFiltered={}", from, size, totalFiltered);

        // Если записей меньше чем from, возвращаем пустой список
        if (from >= totalFiltered) {
            log.info("From >= totalFiltered, returning empty list");
            return new ArrayList<>();
        }

        // Берем нужный диапазон
        int toIndex = Math.min(from + size, totalFiltered);
        List<Event> pagedEvents = filteredEvents.subList(from, toIndex);
        log.info("Returning {} events (from {} to {})", pagedEvents.size(), from, toIndex - 1);

        // Преобразуем в DTO
        List<EventFullDto> eventDtos = pagedEvents.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

        // Устанавливаем количество подтвержденных заявок
        Map<Long, Long> confirmed = getConfirmedRequests(pagedEvents);
        eventDtos.forEach(eventDto -> {
            eventDto.setConfirmedRequests(Math.toIntExact(confirmed.getOrDefault(eventDto.getId().longValue(), 0L)));
            if (eventDto.getViews() == null) {
                eventDto.setViews(0);
            }
        });

        return eventDtos;
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
