package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserInputDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserInputDto request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            throw new ValidationException("Пользователь с таким email уже существует: " + request.getEmail());
        });

        User user = UserMapper.toUser(request);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long id, UserInputDto request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));

        userRepository.findByEmail(request.getEmail()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new ValidationException("Email уже используется другим пользователем: " + request.getEmail());
            }
        });

        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getEmail() != null) {
            existing.setEmail(request.getEmail());
        }

        userRepository.update(existing);
        return UserMapper.toUserDto(existing);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(id);
    }
}