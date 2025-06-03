package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventParameters {
    List<Integer> users;
    List<EventState> states;
    List<Integer> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Integer from = 0;
    Integer size = 10;

    public void setSize(Integer size) {
        this.size = size != null ? size : 10;
    }

    public void setFrom(Integer from) {
        this.from = from != null ? from : 0;
    }
}
