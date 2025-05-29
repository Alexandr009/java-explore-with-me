package ru.practicum.event.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsConfirmed {
    private Long eventId;
    private Long confirmedRequests;
}

