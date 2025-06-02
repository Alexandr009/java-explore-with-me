package ru.practicum.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatisticClient;
import ru.practicum.dto.StatisticDto;
import ru.practicum.dto.StatisticInfoDto;
import ru.practicum.event.model.Event;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class StatClientService {

    private final StatisticClient statisticClient;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Map<Long, Long> getEventsView(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();

        List<Event> publishedEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());

        if (publishedEvents.isEmpty()) {
            return views;
        }

        Optional<LocalDateTime> minPublishedOn = publishedEvents.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (minPublishedOn.isEmpty()) {
            return views;
        }

        LocalDateTime start = minPublishedOn.get();
        LocalDateTime end = LocalDateTime.now();

        List<String> uris = publishedEvents.stream()
                .map(Event::getId)
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        List<StatisticInfoDto> stats = getStatistic(start, end, uris, true);

        stats.forEach(stat -> {
            try {
                String[] uriParts = stat.getUri().split("/");
                if (uriParts.length >= 3) {
                    Long eventId = Long.parseLong(uriParts[2]);
                    views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
                }
            } catch (NumberFormatException e) {
                log.warn("Cannot parse event ID from URI: {}", stat.getUri());
            }
        });

        return views;
    }

    public void createHit(HttpServletRequest request) {
        StatisticDto statisticDto = new StatisticDto();
        statisticDto.setApp("ewm-main-service");
        statisticDto.setUri(request.getRequestURI());
        statisticDto.setIp(request.getRemoteAddr());
        statisticDto.setTimestamp(LocalDateTime.now().format(FORMATTER));

        try {
            statisticClient.postHit(statisticDto);
            log.debug("Statistic hit created: {}", statisticDto);
        } catch (Exception e) {
            log.error("Failed to create statistic hit: {}", e.getMessage());
        }
    }

    private List<StatisticInfoDto> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        try {
            ResponseEntity<Object> response = statisticClient.getStats(start, end, uris, unique);
            if (response.getBody() != null) {
                return Arrays.asList(objectMapper.readValue(
                        objectMapper.writeValueAsString(response.getBody()),
                        StatisticInfoDto[].class));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse statistics response: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to get statistics: {}", e.getMessage());
        }
        return Collections.emptyList();
    }
}