package ru.practicum.user.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserFullDto;
import ru.practicum.user.model.User;
import ru.practicum.user.dto.UserFollowersDto;

import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class Mapper {
    public static UserFullDto toUserFullDto(User user) {
        UserFullDto userFullDto = new UserFullDto();
        userFullDto.setId(user.getId());
        userFullDto.setName(user.getName());
        userFullDto.setEmail(user.getEmail());
        return userFullDto;
    }

    public static UserFollowersDto toUserFollowers(User user) {
        UserFollowersDto userFollowersDto = new UserFollowersDto();
        userFollowersDto.setId(Long.valueOf(user.getId()));
        userFollowersDto.setName(user.getName());
        userFollowersDto.setEmail(user.getEmail());
        userFollowersDto.setFollowers(user.getFollower().stream().map(Mapper::toUserFullDto).collect(Collectors.toList()));
        return userFollowersDto;
    }
}
