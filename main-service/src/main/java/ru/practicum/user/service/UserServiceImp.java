package ru.practicum.user.service;


import org.springframework.stereotype.Service;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserFollowersDto;
import ru.practicum.user.dto.UserFullDto;
import ru.practicum.user.dto.UserPostDto;
import ru.practicum.user.mapper.Mapper;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserParameters;
import ru.practicum.user.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final Mapper mapper;

    public UserServiceImp(UserRepository userRepository, Mapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public UserFullDto createUser(UserPostDto userPostDto) {
        User user = new User();
        user.setName(userPostDto.getName());
        user.setEmail(userPostDto.getEmail());
        List<User> userCheckEmail = userRepository.findUserByEmail(user.getEmail());
        if (!userCheckEmail.isEmpty()) {
            throw new ConflictException("User with this email already exists");
        }

        return Mapper.toUserFullDto(userRepository.save(user));
    }

    @Override
    public List<UserFullDto> getUserByParameters(UserParameters userParameters) {
        List<User> user = userRepository.findAll();

        user = user.stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());

        if (userParameters.getIds() != null) {
            user = user.stream().filter(u -> userParameters.getIds().contains(u.getId())).collect(Collectors.toList());
        } else {
            user = user.stream().skip(userParameters.getFrom())
                    .limit(userParameters.getSize())
                    .collect(Collectors.toList());
        }

        List<UserFullDto> userFullDtos = user.stream().map(u -> Mapper.toUserFullDto(u)).collect(Collectors.toList());

        return userFullDtos;
    }

    @Override
    public void deletedUser(Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        userRepository.delete(user);
    }

    @Override
    public UserFollowersDto addFollower(Long userId, Long followerId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        User follower = userRepository.findById(followerId).orElseThrow(() -> new NotFoundException(String.format("follower id = %d not found", followerId)));
        checkFollower(userId, followerId);
        user.getFollower().add(follower);
        UserFollowersDto userFollowersDto = Mapper.toUserFollowers(userRepository.save(user));

        return userFollowersDto;
    }

    @Override
    public UserFollowersDto getUserFollowers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        UserFollowersDto userFollowersDto = Mapper.toUserFollowers(user);
        return userFollowersDto;
    }

    @Override
    public void removeFollower(Long userId, Long followerId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        User follower = userRepository.findById(followerId).orElseThrow(() -> new NotFoundException(String.format("follower id = %d not found", followerId)));
        user.getFollower().remove(follower);
        userRepository.save(user);
    }

    private void checkFollower(Long userId, Long followerId) {
        if (userId.equals(followerId)) {
            throw new ConflictException("User cannot subscribe to himself");
        }
    }
}
