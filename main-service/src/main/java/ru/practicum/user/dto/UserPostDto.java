package ru.practicum.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPostDto {
    @NonNull
    String name;
    @NonNull
    String email;
}
