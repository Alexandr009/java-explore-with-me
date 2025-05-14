package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticDto {
    @NonNull
    String app;
    @NonNull
    String uri;
    @NonNull
    String ip;
    @NonNull
    String timestamp;
}
