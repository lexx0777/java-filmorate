package ru.yandex.practicum.filmorate;

//import org.junit.jupiter.api.*;

//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

//import static org.junit.jupiter.api.Assertions.*;

//import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.service.UserService;

//import java.util.Arrays;
import java.util.stream.Stream;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Sql(scripts = "/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
/*
    @Test
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].name").value("testname2"))
                .andExpect(jsonPath("$[2].email").value("test3@mail.ru"))
                .andExpect(jsonPath("$[2].login").value("testlogin3"));
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
                .andExpect(jsonPath("$.message").value("Юзер с id 200 не найден"));
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
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Test User"));

        assertEquals(userService.getUserById(4L).getLogin(), "testlogin");
    }

    @Test
    void addUserWithoutName() throws Exception {
        String json = "{\n" +
                "  \"login\": \"testloginname\",\n" +
                "  \"email\": \"testloginname@mail.ru\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("testloginname"));
        assertEquals(userService.getUserById(4L).getName(), "testloginname");
    }

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
        assertEquals("testlogin1upd", userService.getUserById(1L).getLogin());
        assertEquals("testname1 upd", userService.getUserById(1L).getName());
        assertEquals("testupd@mail.ru", userService.getUserById(1L).getEmail());
        assertEquals("1978-10-21", userService.getUserById(1L).getBirthday().toString());
    }

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
                .andExpect(jsonPath("$.name").value("testlogin1upd"))
                .andExpect(jsonPath("$.email").value("testupd@mail.ru"))
                .andExpect(jsonPath("$.login").value("testlogin1upd"))
                .andExpect(jsonPath("$.birthday").value("1978-10-21"));
        assertEquals("testlogin1upd", userService.getUserById(1L).getLogin());
        assertEquals("testlogin1upd", userService.getUserById(1L).getName());
        assertEquals("testupd@mail.ru", userService.getUserById(1L).getEmail());
        assertEquals("1978-10-21", userService.getUserById(1L).getBirthday().toString());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserJsonUpdate")
    void updateUserValidation(String json) throws Exception {
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation error"));

    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserJsonCreate")
    void addUserValidation(String json) throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation error"));

    }

    @Test
    void addUserEmptyJson() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation error"));
    }

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
    }

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
        userService.removeFriend(2L, 3L);
        mockMvc.perform(put("/users/2/friends/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
        assertEquals(1, userService.getUserById(2L).getFriends().size());
        assertEquals(0, userService.getUserById(3L).getFriends().size());
        assertEquals(Arrays.toString(new int[]{3}), userService.getUserById(2L).getFriends().toString());
    }

    @Test
    void removeFriend() throws Exception {
        userService.addFriend(1L, 2L);
        userService.addFriend(1L, 3L);
        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        mockMvc.perform(delete("/users/1/friends/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        assertEquals(0, userService.getUserById(1L).getFriends().size());
        assertEquals(0, userService.getUserById(3L).getFriends().size());
    }

    @Test
    void addUnknownFriend() throws Exception {
        mockMvc.perform(put("/users/1/friends/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Юзер с id 10 не найден"));
    }

    @Test
    void addFriendMyself() throws Exception {
        mockMvc.perform(put("/users/1/friends/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Пользователь не может " +
                        "добавить сам себя в друзья"));
    }

    @Test
    void addFriendUserFriend() throws Exception {
        userService.addFriend(1L, 2L);
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Пользователи уже являются друзьями"));
    }

    @Test
    void getUserFriends() throws Exception {
        userService.addFriend(1L, 2L);
        userService.addFriend(1L, 3L);
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].name").value("testname3"));
    }

    @Test
    void getCommonUserFriends() throws Exception {
        userService.addFriend(1L, 3L);
        userService.addFriend(2L, 3L);
        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3));
    }
*/
}