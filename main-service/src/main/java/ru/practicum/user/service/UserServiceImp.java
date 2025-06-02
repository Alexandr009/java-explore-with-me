package ru.practicum.user.service;


import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
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
        return mapper.toUserFullDto(userRepository.save(user));
    }

    @Override
    public List<UserFullDto> getUserByParameters(UserParameters userParameters) {
        List<User> user = userRepository.findAll();

        // Сортируем по ID чтобы последние добавленные были в конце
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

        List<UserFullDto> userFullDtos = user.stream().map(u -> mapper.toUserFullDto(u)).collect(Collectors.toList());

        return userFullDtos;
    }

    @Override
    public void deletedUser(Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        userRepository.delete(user);
    }
}
