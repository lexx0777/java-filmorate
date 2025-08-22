package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    static Stream<String> provideInvalidUserJsonCreate() {
        return Stream.of(
                "{\n" +
                        "  \"login\": \"doloreullamco\",\n" +
                        "  \"name\": \"test User\",\n" +
                        "  \"email\": \"mail.ru\",\n" +
                        "  \"birthday\": \"1980-08-20\"\n" +
                        "}", // Не верный email

                "{\n" +
                        "  \"login\": \"doloreullamco\",\n" +
                        "  \"name\": \"test User\",\n" +
                        "  \"email\": \"\",\n" +
                        "  \"birthday\": \"1980-08-20\"\n" +
                        "}", // пустой email

                "{\n" +
                        "  \"login\": \"dolore ullamco\",\n" +
                        "  \"name\": \"test User\",\n" +
                        "  \"email\": \"mail@mail.ru\",\n" +
                        "  \"birthday\": \"1980-08-20\"\n" +
                        "}", // логин с пробелами

                "{\n" +
                        "  \"login\": \"\",\n" +
                        "  \"name\": \"Nick Name\",\n" +
                        "  \"email\": \"mail@mail.ru\",\n" +
                        "  \"birthday\": \"1946-08-20\"\n" +
                        "}", // пустой логин

                "{\n" +
                        "  \"login\": \"dolore\",\n" +
                        "  \"name\": \"Nick Name\",\n" +
                        "  \"email\": \"mail@mail.ru\",\n" +
                        "  \"birthday\": \"2060-08-20\"\n" +
                        "}" // Дата рождения в будущем
        );
    }

    static Stream<String> provideInvalidUserJsonUpdate() {
        return Stream.of(
                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"login\": \"doloreullamco\",\n" +
                        "  \"name\": \"test User\",\n" +
                        "  \"email\": \"mail.ru\",\n" +
                        "  \"birthday\": \"1980-08-20\"\n" +
                        "}", // Не верный email

                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"login\": \"doloreullamco\",\n" +
                        "  \"name\": \"test User\",\n" +
                        "  \"email\": \"\",\n" +
                        "  \"birthday\": \"1980-08-20\"\n" +
                        "}", // пустой email

                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"login\": \"dolore ullamco\",\n" +
                        "  \"name\": \"test User\",\n" +
                        "  \"email\": \"mail@mail.ru\",\n" +
                        "  \"birthday\": \"1980-08-20\"\n" +
                        "}", // логин с пробелами

                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"login\": \"\",\n" +
                        "  \"name\": \"Nick Name\",\n" +
                        "  \"email\": \"mail@mail.ru\",\n" +
                        "  \"birthday\": \"1946-08-20\"\n" +
                        "}", // пустой логин

                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"login\": \"dolore\",\n" +
                        "  \"name\": \"Nick Name\",\n" +
                        "  \"email\": \"mail@mail.ru\",\n" +
                        "  \"birthday\": \"2060-08-20\"\n" +
                        "}" // Дата рождения в будущем
        );
    }

    @BeforeEach
    void setUp() throws Exception {
        // Создаем пользователей через HTTP API
        createUserViaHttp(1L, "test@mail.ru", "testlogin1", "testname1", "1900-12-25");
        createUserViaHttp(2L, "test2@mail.ru", "testlogin2", "testname2", "1901-10-21");
        createUserViaHttp(3L, "test3@mail.ru", "testlogin3", "testname3", "1900-12-25");
        createUserViaHttp(4L, "test4@mail.ru", "testlogin4", "testname4", "1901-10-21");
    }

    private void createUserViaHttp(Long id, String email, String login, String name, String birthday) throws Exception {
        String json = String.format("{\n" +
                "  \"id\": %d,\n" +
                "  \"login\": \"%s\",\n" +
                "  \"name\": \"%s\",\n" +
                "  \"email\": \"%s\",\n" +
                "  \"birthday\": \"%s\"\n" +
                "}", id, login, name, email, birthday);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    @AfterEach
    void tearDown() {
        userService.clearUsersData();
    }

    @Test
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].name").value("testname2"))
                .andExpect(jsonPath("$[2].email").value("test3@mail.ru"))
                .andExpect(jsonPath("$[3].login").value("testlogin4"));
    }

    @Test
    void getUserById() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("testname1"));
    }

    @Test
    void getUnknownUserById() throws Exception {
        mockMvc.perform(get("/users/200"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not found"))
                .andExpect(jsonPath("$.message").value("Пользователь с id 200 не найден"));
    }

    @Test
    void addUser() throws Exception {
        String json = "{\n" +
                "  \"login\": \"testlogin\",\n" +
                "  \"name\": \"Test User\",\n" +
                "  \"email\": \"mail@mail.ru\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.login").value("testlogin"));

        // Проверяем через HTTP API что пользователь добавлен
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5)) // 4 созданных в setUp + 1 новый
                .andExpect(jsonPath("$[4].login").value("testlogin"));
    }
/*
    @Test
    void addUserWithoutName() throws Exception {
        String json = "{\n" +
                "  \"login\": \"testlogin\",\n" +
                "  \"email\": \"mail@mail.ru\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("testlogin")) // Ожидаем, что name = login
                .andExpect(jsonPath("$.login").value("testlogin"));

        // Проверяем через HTTP API
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[4].name").value("testlogin")); // Проверяем, что name установлен правильно
    }
*/
@Test
void updateUser() throws Exception {
    String json = "{\n" +
            "  \"id\": 1,\n" +
            "  \"login\": \"testlogin1upd\",\n" +
            "  \"name\": \"testname1 upd\",\n" +
            "  \"email\": \"testupd@mail.ru\",\n" +
            "  \"birthday\": \"1978-10-21\"\n" +
            "}";

    mockMvc.perform(put("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("testname1 upd"))
            .andExpect(jsonPath("$.email").value("testupd@mail.ru"))
            .andExpect(jsonPath("$.login").value("testlogin1upd"))
            .andExpect(jsonPath("$.birthday").value("1978-10-21"));

    // Проверяем через HTTP API, а не через сервис
    mockMvc.perform(get("/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.login").value("testlogin1upd"))
            .andExpect(jsonPath("$.name").value("testname1 upd"))
            .andExpect(jsonPath("$.email").value("testupd@mail.ru"))
            .andExpect(jsonPath("$.birthday").value("1978-10-21"));
}
/*
    @Test
    void updateUserWithoutName() throws Exception {
        String json = "{\n" +
                "  \"id\": 1,\n" +
                "  \"login\": \"testlogin1upd\",\n" +
                "  \"email\": \"testupd@mail.ru\",\n" +
                "  \"birthday\": \"1978-10-21\"\n" +
                "}";

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("testlogin1upd")) // name должен быть равен login
                .andExpect(jsonPath("$.email").value("testupd@mail.ru"))
                .andExpect(jsonPath("$.login").value("testlogin1upd"))
                .andExpect(jsonPath("$.birthday").value("1978-10-21"));

        // Проверяем через HTTP API
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testlogin1upd"))
                .andExpect(jsonPath("$.name").value("testlogin1upd")) // name должен быть равен login
                .andExpect(jsonPath("$.email").value("testupd@mail.ru"))
                .andExpect(jsonPath("$.birthday").value("1978-10-21"));
    }
*/
    /*
    @ParameterizedTest
    @MethodSource("provideInvalidUserJsonUpdate")
    void updateUserValidation(String json) throws Exception {
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation error"));
    }
*/
    /*
    @ParameterizedTest
    @MethodSource("provideInvalidUserJsonCreate")
    void addUserValidation(String json) throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation error"));
    }
*/
    /*
    @Test
    void addUserEmptyJson() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation error"));
    }*/
/*
    @Test
    void updateUserWithoutId() throws Exception {
        String json = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"Nick Name\",\n" +
                "  \"email\": \"mail@mail.ru\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation error"));
    }*/
/*
    @Test
    void updateUnknownUser() throws Exception {
        String json = "{\n" +
                "  \"id\": 123,\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"Nick Name\",\n" +
                "  \"email\": \"mail@mail.ru\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not found"))
                .andExpect(jsonPath("$.message").value("Пользователь с id 123 не найден"));
    }
*/
/*
    @Test
    void updateUserEmptyJson() throws Exception {
        String json = "{}";

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation error"));
    }

    @Test
    void addFriend() throws Exception {
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        mockMvc.perform(put("/users/1/friends/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // Проверяем через HTTP API
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(3));
    }

    @Test
    void removeFriend() throws Exception {
        // Сначала добавляем друзей
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/1/friends/3"))
                .andExpect(status().isOk());

        // Затем удаляем
        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        mockMvc.perform(delete("/users/1/friends/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // Проверяем, что друзей нет
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addUnknownFriend() throws Exception {
        mockMvc.perform(put("/users/1/friends/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not found"))
                .andExpect(jsonPath("$.message").value("Юзер с id 10 не найден"));
    }

    @Test
    void addFriendMyself() throws Exception {
        mockMvc.perform(put("/users/1/friends/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.message").value("Пользователь не может добавить сам себя в друзья"));
    }

    @Test
    void addFriendUserFriend() throws Exception {
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.message").value("Пользователи уже являются друзьями"));
    }

    @Test
    void getUserFriends() throws Exception {
        // Добавляем друзей через HTTP API
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/users/1/friends/3"))
                .andExpect(status().isOk());

        // Получаем друзей через HTTP API
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(3));
    }

    @Test
    void getCommonUserFriends() throws Exception {
        // Добавляем друзей через HTTP API
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/1/friends/3"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/4/friends/2"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/4/friends/3"))
                .andExpect(status().isOk());

        // Получаем общих друзей
        mockMvc.perform(get("/users/1/friends/common/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].name").value("testname3"));
    }*/
}