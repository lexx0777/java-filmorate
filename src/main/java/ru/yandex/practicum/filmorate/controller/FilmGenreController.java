package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.service.FilmGenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class FilmGenreController {

    private final FilmGenreService filmGenreService;

    @GetMapping
    public Collection<FilmGenre> getGenres() {
        return filmGenreService.getAllGenres();
    }

    @GetMapping("/{genreId}")
    public FilmGenre getGenre(@PathVariable Long genreId) {
        return filmGenreService.getGenreById(genreId);
    }
}