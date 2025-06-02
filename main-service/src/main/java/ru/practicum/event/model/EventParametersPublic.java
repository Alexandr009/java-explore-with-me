package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventParametersPublic {
    String text;
    List<Integer> categories;
    Boolean paid;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Boolean onlyAvailable;
    String sort;
    Integer from = 0;
    Integer size = 10;

    public void setFrom(Integer from) {
        this.from = from != null ? from : 0;
    }

    public void setSize(Integer size) {
        this.size = size != null ? size : 10;
    }
}
