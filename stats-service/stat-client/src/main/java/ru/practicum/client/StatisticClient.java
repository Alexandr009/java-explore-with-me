package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatisticDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class StatisticClient extends BaseClient {

    public StatisticClient(@Value("${stats-server.url:http://localhost:9090}") String serviceUrl,
                           RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl))
                .build());
    }

    public ResponseEntity<Object> postHit(StatisticDto statisticDto) {
        return post("/hit", statisticDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String path = buildStatsPath(uris != null && !uris.isEmpty());
        Map<String, Object> parameters = buildStatsParameters(start, end, uris, unique);
        return get(path, parameters);
    }

    private String buildStatsPath(boolean hasUris) {
        if (hasUris) {
            return "/stats?start={start}&end={end}&unique={unique}&uris={uris}";
        } else {
            return "/stats?start={start}&end={end}&unique={unique}";
        }
    }

    private Map<String, Object> buildStatsParameters(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (uris != null && !uris.isEmpty()) {
            return Map.of(
                    "start", start.format(formatter),
                    "end", end.format(formatter),
                    "unique", unique,
                    "uris", String.join(",", uris)
            );
        } else {
            return Map.of(
                    "start", start.format(formatter),
                    "end", end.format(formatter),
                    "unique", unique
            );
        }
    }
}