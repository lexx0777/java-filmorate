package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавление нового фильма: {}", film);
        try {
            // Explicit validation
            if (film.getDescription() != null && film.getDescription().length() > 200) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Максимальная длина описания — 200 символов");
            }

            //film.validate();
            film.setId((long) getNextId());
            films.put(film.getId().intValue(), film);
            log.info("Фильм успешно добавлен: {}", film);
            return film;
        } catch (ValidationException e) {
            log.warn("Ошибка валидации при добавлении фильма: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NullPointerException e) {
            log.warn("Ошибка валидации: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата релиза обязательна");
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Обновление фильма с ID {}: {}", newFilm.getId(), newFilm);
        try {
            if (films.containsKey(newFilm.getId())) {
                Film oldFilm = films.get(newFilm.getId());
                newFilm.validate();
                oldFilm.setName(newFilm.getName());
                oldFilm.setDescription(newFilm.getDescription());
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
                oldFilm.setDuration(newFilm.getDuration());
                log.info("Фильм успешно обновлен: {}", oldFilm);
                return oldFilm;
            }
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        } catch (ValidationException e) {
            log.warn("Ошибка валидации при обновлении фильма: {}", e.getMessage());
            throw e;
        }
    }

    // вспомогательный метод для генерации нового id
    private Integer getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return  ++currentMaxId;
    }
}