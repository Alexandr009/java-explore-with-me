package ru.practicum.user.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserFullDto;
import ru.practicum.user.model.User;

@Component
@NoArgsConstructor
public class Mapper {
    public UserFullDto toUserFullDto(User user) {
        UserFullDto userFullDto = new UserFullDto();
        userFullDto.setId(user.getId());
        userFullDto.setName(user.getName());
        userFullDto.setEmail(user.getEmail());
        return userFullDto;
    }
}
