package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private Validator validator;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void findAllShouldReturnEmptyCollectionInitially() {
        Collection<Film> films = filmController.findAll();
        assertTrue(films.isEmpty(), "Should return empty collection initially");
    }

    @Test
    void addFilmShouldAssignIdAndAddToStorage() {
        Film film = createValidFilm();
        Film createdFilm = filmController.addFilm(film);

        assertNotNull(createdFilm.getId(), "Should assign ID to created film");
        assertEquals(1, filmController.findAll().size(), "Should add film to storage");
    }

    @Test
    void updateFilmShouldModifyExistingFilm() {
        Film original = filmController.addFilm(createValidFilm());
        Film updated = createValidFilm();
        updated.setId(original.getId());
        updated.setName("Updated Name");

        Film result = filmController.updateFilm(updated);
        assertEquals("Updated Name", result.getName(), "Should update film name");
    }

    @Test
    void updateFilmShouldThrowWhenIdNotFound() {
        Film film = createValidFilm();
        film.setId(999);

        assertThrows(NotFoundException.class, () -> filmController.updateFilm(film),
                "Should throw when updating non-existent film");
    }

    @Test
    void findFilmByIdShouldReturnExistingFilm() {
        Film film = filmController.addFilm(createValidFilm());
        Film found = filmController.findFilmById(film.getId());

        assertEquals(film.getId(), found.getId(), "Should return film by ID");
    }

    @Test
    void findFilmByIdShouldThrowWhenNotFound() {
        assertThrows(NotFoundException.class, () -> filmController.findFilmById(999),
                "Should throw when film not found");
    }

    @Test
    void shouldRejectFilmWithTooLongDescription() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(201));

        // First verify the model validation fails
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Model validation should fail for long description");
        assertEquals(1, violations.size());
        assertEquals("Максимальная длина описания — 200 символов", violations.iterator().next().getMessage());

        // Then verify the controller rejects it
        assertThrows(ResponseStatusException.class, () -> filmController.addFilm(film),
                "Controller should reject film with too long description");
    }

    @Test
    void shouldRejectFilmWithEarlyReleaseDate() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        // Change from ValidationException to ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> filmController.addFilm(film), "Should reject film with release date before 1895-12-28");
    }

    @Test
    void shouldAcceptFilmWithBoundaryReleaseDate() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Assertions.assertDoesNotThrow(() -> filmController.addFilm(film),
                "Should accept film with boundary release date");
    }

    @ParameterizedTest
    @MethodSource("invalidFilmsProvider")
    void shouldRejectInvalidFilms(Film invalidFilm) {
        // First verify model validation
        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        if (!violations.isEmpty()) {
            // If Jakarta validation fails, controller should reject
            assertThrows(ResponseStatusException.class, () -> filmController.addFilm(invalidFilm),
                    "Controller should reject invalid film");
        } else {
            // For custom validation rules (like release date)
            assertThrows(ResponseStatusException.class, () -> filmController.addFilm(invalidFilm),
                    "Controller should reject invalid film");
        }
    }

    static Stream<Film> invalidFilmsProvider() {
        return Stream.of(
                createFilm("", "desc", 100, LocalDate.of(2000, 1, 1)), // Empty name
                createFilm("Name", "a".repeat(201), 100, LocalDate.of(2000, 1, 1)), // Long description
                createFilm("Name", "desc", 0, LocalDate.of(2000, 1, 1)), // Zero duration
                createFilm("Name", "desc", -100, LocalDate.of(2000, 1, 1)), // Negative duration
                createFilm("Name", "desc", 100, null) // Null release date
        );
    }

    private static Film createValidFilm() {
        return createFilm("Valid Film", "Description", 120, LocalDate.of(2000, 1, 1));
    }

    private static Film createFilm(String name, String description, int duration, LocalDate releaseDate) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setDuration(duration);
        film.setReleaseDate(releaseDate);
        return film;
    }


}