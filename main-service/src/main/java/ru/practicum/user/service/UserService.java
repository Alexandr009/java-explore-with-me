package ru.practicum.user.service;

import ru.practicum.user.dto.UserPostDto;
import ru.practicum.user.model.User;

public interface UserService {
    public User createUser(UserPostDto userPostDto);
}
