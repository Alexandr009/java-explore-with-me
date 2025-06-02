package ru.practicum.complication.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateComplicationDtoIn {

    @Size(min = 1, max = 50)
    String title;
    List<Long> events;
    Boolean pinned;
}
