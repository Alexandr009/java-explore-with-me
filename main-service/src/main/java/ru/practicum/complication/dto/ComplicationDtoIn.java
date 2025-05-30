package ru.practicum.complication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComplicationDtoIn {
    @NotBlank
    @NotNull
    @Size(min = 1, max = 50)
    String title;
    List<Long> events;
    Boolean pinned;
}
