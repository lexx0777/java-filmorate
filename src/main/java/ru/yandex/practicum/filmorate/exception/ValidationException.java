package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    String name = "validation error";

    public ValidationException(String message) {
        super(message);
    }
}