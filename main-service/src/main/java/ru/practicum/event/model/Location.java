package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Location {
    Double lat;
    Double lon;
}
