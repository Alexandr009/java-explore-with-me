package ru.practicum.user.service;

import ru.practicum.user.dto.UserFullDto;
import ru.practicum.user.dto.UserPostDto;
import ru.practicum.user.model.UserParameters;

import java.util.List;


public interface UserService {
    public UserFullDto createUser(UserPostDto userPostDto);
    public List<UserFullDto> getUserByParameters(UserParameters userParameters);
    public void deletedUser(Long userId);
}
