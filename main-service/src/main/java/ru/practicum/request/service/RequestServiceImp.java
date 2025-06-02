package ru.practicum.request.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.event.dto.EventRequestStatusUpdateDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequestServiceImp implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RequestServiceImp(RequestRepository requestRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }


    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        if (eventId == null || userId == null) {
            throw new ValidationException(String.format("Event ID and/or User ID must not be null. Provided: eventId=%s, userId=%s", eventId, userId));
        }

        if (requestRepository.findByRequester_IdAndEvent_Id(userId, eventId) != null) {
            throw new ConflictException(String.format("Duplicate request not allowed. Request already exists for eventId=%d, userId=%d", eventId, userId));
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d not found", eventId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d not found", userId)));

        Long initiatorId = Long.valueOf(event.getInitiator().getId());
        if (initiatorId.equals(userId)) {
            throw new ConflictException(String.format("Event initiator cannot submit a participation request. eventId=%d, userId=%d", eventId, userId));
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(String.format("Event is not published. eventId=%d", eventId));
        }

        if (event.getParticipantLimit() != 0 &&
                event.getParticipantLimit() <= requestRepository.countAllByEventIdAndStatus(eventId, StatusRequest.CONFIRMED)) {
            throw new ConflictException(String.format("Participant limit exceeded for eventId=%d", eventId));
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
        List<RequestDto> requestDtoList = requestRepository.findAllByEvent_Initiator_IdAndEvent_Id(userId, eventId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
        return requestDtoList;
    }

    @Override
    public RequestStatusUpdateDto updateParticipationRequest(Long userId, Long eventId, EventRequestStatusUpdateDto eventRequestStatusUpdateDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("request id = %d not found", eventId)));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("user id = %d not found", userId)));

        List<Long> requestIds = eventRequestStatusUpdateDto.getRequestIds();
        StatusRequest status = eventRequestStatusUpdateDto.getStatus();

        List<Request> requestsAll = requestRepository.findAll();

        List<Request> requests = requestsAll.stream()
                .filter(request -> requestIds.contains(request.getId()))
                .collect(Collectors.toList());

        List<RequestDto> confirmedList = new ArrayList<>();
        List<RequestDto> rejectedList = new ArrayList<>();

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= requestRepository.countAllByEventIdAndStatus(eventId, StatusRequest.CONFIRMED)) {
            throw new ConflictException("Over limit");
        }

        if (requestIds != null) {
            requestIds.forEach(id -> {
                Request request = requests.stream()
                        .filter(request1 -> request1.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException(String.format("request id = %d not found", id)));
                if (!request.getStatus().equals(StatusRequest.PENDING)) {
                    throw new ConflictException("request cannot be confirmed");
                }
                if (status.equals(StatusRequest.CONFIRMED)) {
                    request.setStatus(StatusRequest.CONFIRMED);
                    confirmedList.add(RequestMapper.toDto(requestRepository.save(request)));
                } else {
                    request.setStatus(StatusRequest.REJECTED);
                    rejectedList.add(RequestMapper.toDto(requestRepository.save(request)));
                }
            });
        }
        eventRepository.save(event);
        RequestStatusUpdateDto requestStatusUpdateDto = new RequestStatusUpdateDto(confirmedList, rejectedList);
        return requestStatusUpdateDto;
    }

    @Override
    public RequestDto updateRequestStatus(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(String.format("request id = %d not found", requestId)));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("user id = %d not found", userId)));
        request.setStatus(StatusRequest.CANCELED);
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getRequestsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("User not found id = %d", userId)));
        return requestRepository.findAll().stream()
                .filter(request -> Objects.equals(request.getRequester().getId(), user.getId()))
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }
}
