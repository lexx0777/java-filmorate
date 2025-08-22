package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class OtherException extends RuntimeException {
    String name = "server error";

    public OtherException(String message) {
        super(message);
    }
}