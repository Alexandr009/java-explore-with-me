package ru.practicum.request.dtro;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.request.model.StatusRequest;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDto {
    Long id;
    Long event;
    LocalDateTime created;
    Long requester;
    StatusRequest status;
}
