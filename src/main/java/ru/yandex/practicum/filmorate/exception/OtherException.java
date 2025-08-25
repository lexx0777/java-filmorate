package ru.yandex.practicum.filmorate.exception;

public class OtherException extends RuntimeException {
    String name = "server error";

    public OtherException(String message) {
        super(message);
    }
}