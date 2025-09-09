package ru.yandex.practicum.filmorate.service;

//import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.OtherException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;

@Service
@Slf4j
//@RequiredArgsConstructor
public class FilmService {
    //@Qualifier("filmDbStorage") // ← добавьте эту аннотацию
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final Comparator<Film> filmComparator = Comparator.comparing((Film film) -> film.getUsersLikes().size()).reversed();

    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilm(id).orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public Film createFilm(Film film) {
        log.info("Добавлен фильм: {}", film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("В запросе на обновление фильма не передан id");
            throw new ValidationException("Id не может быть пустым");
        }

        filmStorage.getFilm(newFilm.getId())
                .orElseThrow(() -> {
                    log.warn("В запросе на обновление фильма передан неизвестный id - {}", newFilm.getId());
                    return new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
                });

        return filmStorage.updateFilm(newFilm);
    }

    public void likeFilm(Long filmId, Long userId) {
        if (!getFilmById(filmId).getUsersLikes().add(userService.getUserById(userId).getId())) {
            log.warn("Пользователь с id {} уже ставил лайк фильму с id {}", userId, filmId);
            throw new OtherException("Пользователь уже ставил лайк фильму");
        }

        // Добавляем лайк в БД
        filmStorage.addLikeFilm(filmId, userId);

        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    public void removeLikeFilm(Long filmId, Long userId) {
        if (!getFilmById(filmId).getUsersLikes().remove(userService.getUserById(userId).getId())) {
            log.warn("Пользователь с id {} не ставил лайк фильму id {}", userId, filmId);
            throw new OtherException("Пользователь не ставил лайк фильму");
        }

        // Удаляем лайк из БД
        filmStorage.deleteLikeFilm(filmId, userId);

        log.info("Пользователь с id {} удалил свой лайк у фильма с id {}", userId, filmId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getFilms().stream().sorted(filmComparator).limit(count).toList();
    }

    public void clearFilmsData() {
        filmStorage.clearData();
    }
}