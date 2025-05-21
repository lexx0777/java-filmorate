package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    private int nextId = 1;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавление нового фильма: {}", film);
        try {
            film.validate();
            film.setId(nextId++);
            films.add(film);
            log.info("Фильм успешно добавлен: {}", film);
            return film;
        } catch (ValidationException e) {
            log.warn("Ошибка валидации при добавлении фильма: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновление фильма с ID {}: {}", film.getId(), film);
        try {
            film.validate();
            // логика обновления
            log.info("Фильм успешно обновлен: {}", film);
            return film;
        } catch (ValidationException e) {
            log.warn("Ошибка валидации при обновлении фильма: {}", e.getMessage());
            throw e;
        }
    }
}