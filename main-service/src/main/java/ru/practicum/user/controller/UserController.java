package ru.practicum.user.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserFullDto;
import ru.practicum.user.dto.UserPostDto;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserParameters;
import ru.practicum.user.service.UserService;

import java.util.List;

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
    public UserFullDto createUser(@RequestBody UserPostDto userDto) {
        log.info("Creating user: {}", userDto);
        UserFullDto userFullDto = userService.createUser(userDto);
        return  userFullDto;
    }

    @GetMapping
    public List<UserFullDto> getUsers (@RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size, @RequestParam(required = false) List<Integer> ids) {
        log.info("Getting users: {}", ids, from, size);
        UserParameters userParameters = new UserParameters();
        userParameters.setFrom(from);
        userParameters.setSize(size);
        userParameters.setIds(ids);
        List<UserFullDto> userFullDto = userService.getUserByParameters(userParameters);
        return userFullDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") long id) {
        log.info("Deleting user: {}", id);
        userService.deletedUser(id);
    }
}
