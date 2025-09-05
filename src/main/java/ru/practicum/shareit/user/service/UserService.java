package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserInputDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserInputDto request);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long id, UserInputDto request);

    void deleteUser(Long id);
}