package ru.practicum.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.user.dto.UserPostDto;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(UserPostDto userPostDto) {
        User user = new User();
        user.setName(userPostDto.getName());
        user.setEmail(userPostDto.getEmail());
        return userRepository.save(user);
    }
}
