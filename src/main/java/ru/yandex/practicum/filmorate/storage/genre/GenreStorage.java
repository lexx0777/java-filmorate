package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {

    Optional<FilmGenre> getGenre(Long id);

    Collection<FilmGenre> getGenres();
}