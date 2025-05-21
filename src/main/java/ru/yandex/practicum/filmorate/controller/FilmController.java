package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/films")
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    private int nextId = 1;

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        film.setId(nextId++);
        films.add(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.set(i, film);
                return film;
            }
        }
        throw new RuntimeException("Film not found with id: " + film.getId());
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films);
    }
}