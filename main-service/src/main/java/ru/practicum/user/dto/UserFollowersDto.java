package ru.practicum.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFollowersDto {
    private Long id;
    private String name;
    private String email;
    private List<UserFullDto> followers;

}
