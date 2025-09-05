package ru.practicum.shareit.common;


import ru.practicum.shareit.exeption.NotFoundException;

import java.util.Optional;

public abstract class BaseService {

    protected <T> T getOrThrow(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new NotFoundException(message));
    }
}