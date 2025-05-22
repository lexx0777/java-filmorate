package ru.yandex.practicum.filmorate.model;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Data
@Slf4j
public class Film {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    public void validate() {
        if (releaseDate == null) {
            log.error("Validation failed: Дата релиза обязательна");
            throw new ValidationException("Дата релиза обязательна");
        }
        if (name == null || name.isEmpty()) {
            log.error("Validation failed: Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (duration <= 0) {
            log.error("Validation failed: Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Validation failed: Дата релиза {} раньше допустимой (1895-12-28)", releaseDate);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}