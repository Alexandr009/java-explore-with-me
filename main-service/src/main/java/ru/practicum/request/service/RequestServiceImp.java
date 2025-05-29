package ru.practicum.request.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.event.dto.EventRequestStatusUpdateDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConditionsNotMetException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dtro.RequestDto;
import ru.practicum.request.dtro.RequestStatusUpdateDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.StatusRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequestServiceImp implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RequestServiceImp (RequestRepository requestRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }


    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        if (eventId == null || userId == null) {
            throw new ConditionsNotMetException(String.format("Event ID and/or User ID must not be null. Provided: eventId=%s, userId=%s", eventId, userId));
        }

        if (requestRepository.findByRequester_IdAndEvent_Id(userId, eventId) != null) {
            throw new ValidationException(String.format("Duplicate request not allowed. Request already exists for eventId=%d, userId=%d", eventId, userId));
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d not found", eventId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d not found", userId)));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException(String.format("Event initiator cannot submit a participation request. eventId=%d, userId=%d", eventId, userId));
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException(String.format("Event is not published. eventId=%d", eventId));
        }

        if (event.getParticipantLimit() != 0 &&
                event.getParticipantLimit() <= requestRepository.countAllByEventIdAndStatus(eventId, StatusRequest.CONFIRMED)) {
            throw new ValidationException(String.format("Participant limit exceeded for eventId=%d", eventId));
        }

        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);
        request.setEvent(event);

        log.info("request Moderation: " + event.getRequestModeration());

        if (Boolean.TRUE.equals(event.getRequestModeration()) && event.getParticipantLimit() > 0) {
            request.setStatus(StatusRequest.PENDING);
        } else {
            request.setStatus(StatusRequest.CONFIRMED);
        }

        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getParticipationRequests(Long userId, Long eventId) {
        return List.of();
    }

    @Override
    public RequestStatusUpdateDto updateParticipationRequest(Long userId, Long eventId, EventRequestStatusUpdateDto eventRequestStatusUpdateDto) {
        return null;
    }

    @Override
    public RequestDto updateRequestStatus(Long userId, Long requestId) {
        return null;
    }

    @Override
    public List<RequestDto> getRequestsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("User not found id = %d", userId)));
        return requestRepository.findAll().stream()
                .filter(request -> request.getRequester().getId() == user.getId())
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }
}
