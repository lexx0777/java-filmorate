package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    public String name = "not found";

    public NotFoundException(String message) {
        super(message);
    }
}