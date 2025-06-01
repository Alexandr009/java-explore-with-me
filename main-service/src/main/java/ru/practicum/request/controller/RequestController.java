package ru.practicum.request.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dtro.RequestDto;
import ru.practicum.request.service.RequestServiceImp;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class RequestController {
    private final RequestServiceImp requestService;
    public RequestController(RequestServiceImp requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable("userId") Long userId,
                                    @RequestParam(required = false) Long eventId) {
        log.info("Add request userId: {} eventID: {}",userId, eventId);
        RequestDto requestDto =requestService.createRequest(userId, eventId);
        return requestDto;
    }
//
    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto updateRequest(@PathVariable("userId") Long userId,
                                                    @PathVariable("requestId") Long requestId) {
        log.info("Cancel request requestId: " + requestId + " userId: " + userId);
        RequestDto requestDto = requestService.updateRequestStatus(userId, requestId);
        return requestDto;
    }

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getRequestsByUser(@PathVariable("userId") Long userId) {
        log.info("Get request by current user with strangers events {}",userId);
        List<RequestDto> requestDtos = requestService.getRequestsByUser(userId);
        return requestDtos;
    }

}
