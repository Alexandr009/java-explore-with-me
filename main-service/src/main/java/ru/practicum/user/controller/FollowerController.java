package ru.practicum.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserFollowersDto;
import ru.practicum.user.service.UserService;

@Slf4j
@RestController
@RequestMapping("/users")
public class FollowerController {

    public final UserService userService;

    public FollowerController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{userId}/follower/{followerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserFollowersDto addFollower(@PathVariable Long userId,
                                        @PathVariable Long followerId) {
        log.info("Adding follower {} to user {}", followerId, userId);
        UserFollowersDto userFollowersDto = userService.addFollower(userId, followerId);
        return userFollowersDto;
    }

    @GetMapping("/{userId}/follower")
    public UserFollowersDto getFollowers(@PathVariable Long userId) {
        log.info("Getting followers {}", userId);
        UserFollowersDto userFollowersDto = userService.getUserFollowers(userId);
        return userFollowersDto;
    }

    @DeleteMapping("/{userId}/follower/{followerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFollower(@PathVariable Long userId,
                               @PathVariable Long followerId) {
        log.info("Deleting follower {} from user {}", followerId, userId);
        userService.removeFollower(userId, followerId);
    }
}
