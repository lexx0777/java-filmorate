package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        filmService.createFilm(new Film(1L, "test1", "test_descr1", LocalDate.of(1900, 12, 25), 10));
        filmService.createFilm(new Film(2L, "test2", "test_descr2", LocalDate.of(1900, 12, 25), 10));
        userService.createUser(new User(1L, "test@mail.ru", "testlogin1", "testname1", LocalDate.of(1900, 12, 25)));
        userService.createUser(new User(2L, "test2@mail.ru", "testlogin2", "testname2", LocalDate.of(1901, 10, 21)));
    }

    @AfterEach
    void tearDown() {
        filmService.clearFilmsData();
        userService.clearUsersData();
    }

    static Stream<String> provideInvalidFilmJsonCreate() {
        return Stream.of(
                "{\n" +
                        "  \"name\": \"\",\n" +
                        "  \"description\": \"Sci-fi action\",\n" +
                        "  \"releaseDate\": \"1900-12-25\",\n" +
                        "  \"duration\": 10\n" +
                        "}", // Пустое название

                "{\n" +
                        "  \"name\": \"The Matrix\",\n" +
                        "  \"description\": \"eVsHnlJqcle8LzLbMyajlPHJvq6xPAEBqvi0V55TAlZ6vokGPaTGgzI9ypdZv8X5X2StI10zqReOEy" +
                        "mkWTNcxCFbRqrmJBVrIdgQf9VDYXcE76DmMQ7jI4jzHZ0XKxx13dlujL9A3MbNudpQC4Uw1aoIsOoD" +
                        "cHS1JAHFXAEduHxtxNuo3Iq6tGj0xXZqDczZX2UqqTfmt\",\n" +
                        "  \"releaseDate\": \"1900-12-25\",\n" +
                        "  \"duration\": 10\n" +
                        "}", // Описание длиной 201 символ

                "{\n" +
                        "  \"name\": \"The Matrix\",\n" +
                        "  \"description\": \"NQ1qwgfJURRuOBe7rZJuRMNP4wCGFHKvrz5flsvTzz5NKXwP1II7Jytjjwn51G51eQBF2IfydfdRrkdq" +
                        "50DSPqWqOFri415GQb8HgwqI1ET0uF5wDFBS9iwzCysqqSTffd0BcRf1zHwPjMwDSOnQHETMtSEYTd" +
                        "V7PLRlZw4mgreyXaFpSjVXB4149KfTyeFL9YFXSANQbCly2IwtFAftdPKSTHrLzNFk2AEwInZGATHu" +
                        "q73LwzP5qmx89M\",\n" +
                        "  \"releaseDate\": \"1900-12-25\",\n" +
                        "  \"duration\": 10\n" +
                        "}", // Описание длиной 300 символов

                "{\n" +
                        "  \"name\": \"The Matrix\",\n" +
                        "  \"description\": \"Sci-fi action\",\n" +
                        "  \"releaseDate\": \"1800-12-25\",\n" +
                        "  \"duration\": 10\n" +
                        "}", // Дата раньше 1895

                "{\n" +
                        "  \"name\": \"The Matrix\",\n" +
                        "  \"description\": \"Sci-fi action\",\n" +
                        "  \"releaseDate\": \"1900-12-25\",\n" +
                        "  \"duration\": -5\n" +
                        "}" // Отрицательная длительность
        );
    }

    static Stream<String> provideInvalidFilmJsonUpdate() {
        return Stream.of(
                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"\",\n" +
                        "  \"description\": \"Sci-fi action\",\n" +
                        "  \"releaseDate\": \"1900-12-25\",\n" +
                        "  \"duration\": 10\n" +
                        "}", // Пустое название

                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"The Matrix\",\n" +
                        "  \"description\": \"eVsHnlJqcle8LzLbMyajlPHJvq6xPAEBqvi0V55TAlZ6vokGPaTGgzI9ypdZv8X5X2StI10zqReOEy" +
                        "mkWTNcxCFbRqrmJBVrIdgQf9VDYXcE76DmMQ7jI4jzHZ0XKxx13dlujL9A3MbNudpQC4Uw1aoIsOoD" +
                        "cHS1JAHFXAEduHxtxNuo3Iq6tGj0xXZqDczZX2UqqTfmt\",\n" +
                        "  \"releaseDate\": \"1900-12-25\",\n" +
                        "  \"duration\": 10\n" +
                        "}", // Описание длиной 201 символ

                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"The Matrix\",\n" +
                        "  \"description\": \"NQ1qwgfJURRuOBe7rZJuRMNP4wCGFHKvrz5flsvTzz5NKXwP1II7Jytjjwn51G51eQBF2IfydfdRrkdq" +
                        "50DSPqWqOFri415GQb8HgwqI1ET0uF5wDFBS9iwzCysqqSTffd0BcRf1zHwPjMwDSOnQHETMtSEYTd" +
                        "V7PLRlZw4mgreyXaFpSjVXB4149KfTyeFL9YFXSANQbCly2IwtFAftdPKSTHrLzNFk2AEwInZGATHu" +
                        "q73LwzP5qmx89M\",\n" +
                        "  \"releaseDate\": \"1900-12-25\",\n" +
                        "  \"duration\": 10\n" +
                        "}", // Описание длиной 300 символов

                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"The Matrix\",\n" +
                        "  \"description\": \"Sci-fi action\",\n" +
                        "  \"releaseDate\": \"1800-12-25\",\n" +
                        "  \"duration\": 10\n" +
                        "}", // Дата раньше 1895

                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"The Matrix\",\n" +
                        "  \"description\": \"Sci-fi action\",\n" +
                        "  \"releaseDate\": \"1900-12-25\",\n" +
                        "  \"duration\": -5\n" +
                        "}", // Отрицательная длительность

                "{\n" +
                        "  \"id\": 135,\n" +
                        "  \"name\": \"The Matrix\",\n" +
                        "  \"description\": \"Sci-fi action\",\n" +
                        "  \"releaseDate\": \"1900-12-25\",\n" +
                        "  \"duration\": -5\n" +
                        "}" // неизвестный ид
        );
    }

    @Test
    void getFilms() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("test1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("test2"));
    }

    @Test
    void getFilmById() throws Exception {
        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test1"));
    }

    @Test
    void getUnknownFilmById() throws Exception {
        mockMvc.perform(get("/films/5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not found"))
                .andExpect(jsonPath("$.message").value("Фильм с id 5 не найден"));
    }

    @Test
    void addFilms() throws Exception {
        String json = "{\n" +
                "  \"name\": \"The Matrix\",\n" +
                "  \"description\": \"Sci-fi action\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("The Matrix"));

        assertEquals(filmService.getFilmById(3L).getName(), "The Matrix");
    }

    @Test
    void updateFilms() throws Exception {
        String json = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"test1_upd\",\n" +
                "  \"description\": \"test_descr1_upd\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test1_upd"));

        assertEquals("test1_upd", filmService.getFilmById(1L).getName());
        assertEquals("test_descr1_upd", filmService.getFilmById(1L).getDescription());
        assertEquals("1967-03-25", filmService.getFilmById(1L).getReleaseDate().toString());
        assertEquals(100, filmService.getFilmById(1L).getDuration());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFilmJsonCreate")
    void addFilmValidation(String json) throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation error"));
    }

    @Test
    void addFilmEmptyJson() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation error"));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFilmJsonUpdate")
    void updateFilmValidation(String json) throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation error"));
    }

    @Test
    void updateFilmWithoutId() throws Exception {
        String json = "{\n" +
                "  \"name\": \"The Matrix\",\n" +
                "  \"description\": \"Sci-fi action\",\n" +
                "  \"releaseDate\": \"1900-12-25\",\n" +
                "  \"duration\": 10\n" +
                "}";

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation error"));
    }

    @Test
    void updateFilmEmptyJson() throws Exception {
        String json = "{}";

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation error"));
    }

    @Test
    void likeFilm() throws Exception {

        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        mockMvc.perform(put("/films/1/like/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        assertEquals(2, filmService.getFilmById(1L).getUsersLikes().size());
    }

    @Test
    void deleteFilm() throws Exception {
        filmService.likeFilm(1L, 1L);
        filmService.likeFilm(1L, 2L);
        mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        mockMvc.perform(delete("/films/1/like/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        assertEquals(0, filmService.getFilmById(1L).getUsersLikes().size());
    }

    @Test
    void likeUnknownFilm() throws Exception {
        mockMvc.perform(put("/films/4/like/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not found"))
                .andExpect(jsonPath("$.message").value("Фильм с id 4 не найден"));
    }

    @Test
    void likeFilmUnkownUser() throws Exception {
        mockMvc.perform(put("/films/1/like/4"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not found"))
                .andExpect(jsonPath("$.message").value("Юзер с id 4 не найден"));
    }

    @Test
    void deleteLikeFilm() throws Exception {
        filmService.likeFilm(1L, 1L);
        mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        assertEquals(0, filmService.getFilmById(1L).getUsersLikes().size());
    }

    @Test
    void deleteUnknownLikeFilm() throws Exception {
        mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("server error"))
                .andExpect(jsonPath("$.message").value("Пользователь не ставил лайк фильму"));
    }

    @Test
    void deleteLikeFilmUnkownUser() throws Exception {
        mockMvc.perform(delete("/films/1/like/4"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not found"))
                .andExpect(jsonPath("$.message").value("Юзер с id 4 не найден"));
    }

    @Test
    void deleteLikeFilmUnkownFilm() throws Exception {
        mockMvc.perform(delete("/films/4/like/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not found"))
                .andExpect(jsonPath("$.message").value("Фильм с id 4 не найден"));
    }

    @Test
    void get3PopularFilms() throws Exception {
        filmService.createFilm(new Film(3L, "test3", "test_descr3", LocalDate.of(1900, 12, 25), 10));
        filmService.createFilm(new Film(4L, "test4", "test_descr4", LocalDate.of(1900, 12, 25), 10));
        userService.createUser(new User(3L, "test3@mail.ru", "testlogin3", "testname1", LocalDate.of(1900, 12, 25)));
        userService.createUser(new User(4L, "test4@mail.ru", "testlogin4", "testname2", LocalDate.of(1901, 10, 21)));
        filmService.createFilm(new Film(5L, "test5", "test_descr5", LocalDate.of(1900, 12, 25), 10));
        filmService.createFilm(new Film(6L, "test6", "test_descr6", LocalDate.of(1900, 12, 25), 10));
        userService.createUser(new User(5L, "test5@mail.ru", "testlogin5", "testname1", LocalDate.of(1900, 12, 25)));
        userService.createUser(new User(6L, "test6@mail.ru", "testlogin6 ", "testname2", LocalDate.of(1901, 10, 21)));
        filmService.likeFilm(3L, 1L);
        filmService.likeFilm(3L, 2L);
        filmService.likeFilm(3L, 3L);
        filmService.likeFilm(3L, 4L);
        filmService.likeFilm(1L, 5L);
        filmService.likeFilm(1L, 6L);
        filmService.likeFilm(4L, 6L);
        filmService.likeFilm(2L, 1L);
        filmService.likeFilm(2L, 2L);
        mockMvc.perform(get("/films/popular?count=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[1].name").value("test1"))
                .andExpect(jsonPath("$[2].description").value("test_descr2"));
    }

    @Test
    void getDefaultPopularFilms() throws Exception {
        filmService.createFilm(new Film(3L, "test3", "test_descr3", LocalDate.of(1900, 12, 25), 10));
        filmService.createFilm(new Film(4L, "test4", "test_descr4", LocalDate.of(1900, 12, 25), 10));
        userService.createUser(new User(3L, "test3@mail.ru", "testlogin3", "testname1", LocalDate.of(1900, 12, 25)));
        userService.createUser(new User(4L, "test4@mail.ru", "testlogin4", "testname2", LocalDate.of(1901, 10, 21)));
        filmService.createFilm(new Film(5L, "test5", "test_descr5", LocalDate.of(1900, 12, 25), 10));
        filmService.createFilm(new Film(6L, "test6", "test_descr6", LocalDate.of(1900, 12, 25), 10));
        userService.createUser(new User(5L, "test5@mail.ru", "testlogin5", "testname1", LocalDate.of(1900, 12, 25)));
        userService.createUser(new User(6L, "test6@mail.ru", "testlogin6 ", "testname2", LocalDate.of(1901, 10, 21)));
        filmService.createFilm(new Film(7L, "test7", "test_descr7", LocalDate.of(1900, 12, 25), 10));
        filmService.createFilm(new Film(8L, "test8", "test_descr8", LocalDate.of(1900, 12, 25), 10));
        userService.createUser(new User(7L, "test7@mail.ru", "testlogin7", "testname1", LocalDate.of(1900, 12, 25)));
        userService.createUser(new User(8L, "test8@mail.ru", "testlogin8", "testname2", LocalDate.of(1901, 10, 21)));
        filmService.createFilm(new Film(9L, "test9", "test_descr9", LocalDate.of(1900, 12, 25), 10));
        filmService.createFilm(new Film(10L, "test10", "test_descr10", LocalDate.of(1900, 12, 25), 10));
        userService.createUser(new User(9L, "test9@mail.ru", "testlogin9", "testname1", LocalDate.of(1900, 12, 25)));
        userService.createUser(new User(10L, "test10@mail.ru", "testlogin10 ", "testname2", LocalDate.of(1901, 10, 21)));
        filmService.createFilm(new Film(11L, "test11", "test_descr11", LocalDate.of(1900, 12, 25), 10));
        filmService.createFilm(new Film(12L, "test12", "test_descr12", LocalDate.of(1900, 12, 25), 10));
        userService.createUser(new User(11L, "test11@mail.ru", "testlogin11", "testname1", LocalDate.of(1900, 12, 25)));
        userService.createUser(new User(12L, "test12@mail.ru", "testlogin12", "testname2", LocalDate.of(1901, 10, 21)));
        filmService.createFilm(new Film(13L, "test13", "test_descr13", LocalDate.of(1900, 12, 25), 10));
        filmService.createFilm(new Film(14L, "test14", "test_descr14", LocalDate.of(1900, 12, 25), 10));
        userService.createUser(new User(13L, "test13@mail.ru", "testlogin13", "testname1", LocalDate.of(1900, 12, 25)));
        userService.createUser(new User(14L, "test14@mail.ru", "testlogin14 ", "testname2", LocalDate.of(1901, 10, 21)));
        filmService.likeFilm(3L, 1L);
        filmService.likeFilm(3L, 2L);
        filmService.likeFilm(3L, 3L);
        filmService.likeFilm(3L, 4L);
        filmService.likeFilm(1L, 5L);
        filmService.likeFilm(1L, 6L);
        filmService.likeFilm(4L, 6L);
        filmService.likeFilm(5L, 1L);
        filmService.likeFilm(5L, 2L);
        filmService.likeFilm(6L, 1L);
        filmService.likeFilm(7L, 2L);
        filmService.likeFilm(8L, 3L);
        filmService.likeFilm(6L, 4L);
        filmService.likeFilm(10L, 5L);
        filmService.likeFilm(11L, 6L);
        filmService.likeFilm(12L, 6L);
        filmService.likeFilm(10L, 1L);
        filmService.likeFilm(10L, 2L);
        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));
    }
}