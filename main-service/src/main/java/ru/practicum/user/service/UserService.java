package ru.practicum.user.service;

import ru.practicum.user.dto.UserFullDto;
import ru.practicum.user.dto.UserPostDto;
import ru.practicum.user.model.UserParameters;

import java.util.List;


public interface UserService {
    UserFullDto createUser(UserPostDto userPostDto);

    List<UserFullDto> getUserByParameters(UserParameters userParameters);

    void deletedUser(Long userId);
}
