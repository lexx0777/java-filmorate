package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;

@Data
@Slf4j
public class User {
    private int id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна содержать символ @")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public String getName() {
        return name == null || name.isBlank() ? login : name;
    }

    public void validate() {
        if (login == null) {
            log.error("Validation failed: Логин не может быть пустым");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (email == null || email.isEmpty()) {
            log.error("Validation failed: email не может быть пустым");
            throw new ValidationException("email не может быть пустым");
        }
        if (!email.contains("@")) {
            log.error("Validation failed: email не корректен");
            throw new ValidationException("email не корректен");
        }
        if (birthday.isAfter(ChronoLocalDate.from(LocalDateTime.now()))) {
            log.error("Validation failed: Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}