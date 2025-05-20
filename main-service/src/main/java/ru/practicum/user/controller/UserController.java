package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserPostDto;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

@RestController
@RequestMapping(path = "/admin/users")
@Slf4j
public class UserController {
    @Autowired
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserPostDto userDto) {
        log.info("Creating user: {}", userDto);
        return userService.createUser(userDto);
    }


}
