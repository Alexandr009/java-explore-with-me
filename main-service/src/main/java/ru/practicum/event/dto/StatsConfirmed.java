package ru.practicum.event.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class StatsConfirmed {
    private Integer eventId;
    private Long confirmedRequests;
}
