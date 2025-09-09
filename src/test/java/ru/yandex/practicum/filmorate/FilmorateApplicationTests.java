package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
		UserDbStorage.class,
		FilmDbStorage.class,
		UserRowMapper.class,
		FilmRowMapper.class,
		MpaDbStorage.class,
		GenreDbStorage.class,
		MpaRowMapper.class,
		GenreRowMapper.class,
})
@Sql(scripts = "/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class FilmorateApplicationTests {
	private final FilmDbStorage filmDbStorage;
	private final UserDbStorage userDbStorage;
	private final GenreDbStorage genreDbStorage;
	private final MpaDbStorage mpaDbStorage;
/*
	@Test
	public void testFindUserById() {
		Optional<User> userOptional = userDbStorage.getUser(1L);
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
	}

	@Test
	public void testFindAllUsers() {
		Collection<User> allUsers = userDbStorage.getUsers();
		assertThat(allUsers)
				.isNotEmpty()
				.hasSize(3)
				.map(User::getId)
				.containsExactly(1L, 2L, 3L);
	}

	@Test
	public void testAddUser() {
		User testUser = new User();
		testUser.setEmail("test@test.com");
		testUser.setLogin("test");
		testUser.setName("test");
		testUser.setBirthday(LocalDate.of(1900, 12, 25));
		userDbStorage.addUser(testUser);
		Optional<User> userOptional = userDbStorage.getUser(4L);
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 4L));

		Collection<User> allUsers = userDbStorage.getUsers();
		assertThat(allUsers)
				.isNotEmpty()
				.hasSize(4)
				.map(User::getId)
				.containsExactly(1L, 2L, 3L, 4L);
	}

	@Test
	public void testAddNotUniqueEmailUser() {
		User testUser = new User(4L, "test1@mail.ru", "test", "test", LocalDate.of(1900, 12, 25));

		assertThatThrownBy(() -> userDbStorage.addUser(testUser))
				.isInstanceOf(DuplicateKeyException.class);

		Collection<User> allUsers = userDbStorage.getUsers();
		assertThat(allUsers)
				.isNotEmpty()
				.hasSize(3);
	}

	@Test
	public void testAddNotUniqueLoginUser() {
		User testUser = new User(4L, "test@test.ru", "testlogin1", "test", LocalDate.of(1900, 12, 25));

		assertThatThrownBy(() -> userDbStorage.addUser(testUser))
				.isInstanceOf(DuplicateKeyException.class);

		Collection<User> allUsers = userDbStorage.getUsers();
		assertThat(allUsers)
				.isNotEmpty()
				.hasSize(3);
	}

	@Test
	public void testAddNullEmailUser() {
		User testUser = new User();
		testUser.setLogin("test_login");
		testUser.setName("test_name");
		testUser.setBirthday(LocalDate.of(1900, 12, 25));
		assertThatThrownBy(() -> userDbStorage.addUser(testUser))
				.isInstanceOf(DataIntegrityViolationException.class);

		Collection<User> allUsers = userDbStorage.getUsers();
		assertThat(allUsers)
				.isNotEmpty()
				.hasSize(3);
	}

	@Test
	public void testAddNullLoginUser() {
		User testUser = new User();
		testUser.setEmail("test_email@mail.ru");
		testUser.setName("test_name");
		testUser.setBirthday(LocalDate.of(1900, 12, 25));
		assertThatThrownBy(() -> userDbStorage.addUser(testUser))
				.isInstanceOf(DataIntegrityViolationException.class);

		Collection<User> allUsers = userDbStorage.getUsers();
		assertThat(allUsers)
				.isNotEmpty()
				.hasSize(3);
	}

	@Test
	public void testUpdateUser() {
		User updUser = new User(1L, "test1@mail.ru_upd", "testlogin1_upd", "testname1_upd", LocalDate.of(1900, 12, 25));

		userDbStorage.updateUser(updUser);

		Optional<User> userOptional = userDbStorage.getUser(1L);
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user -> {
					assertThat(user).hasFieldOrPropertyWithValue("email", "test1@mail.ru_upd");
					assertThat(user).hasFieldOrPropertyWithValue("login", "testlogin1_upd");
					assertThat(user).hasFieldOrPropertyWithValue("name", "testname1_upd");
					assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1900, 12, 25));
				});

		Collection<User> allUsers = userDbStorage.getUsers();
		assertThat(allUsers)
				.isNotEmpty()
				.hasSize(3);
	}

	@Test
	public void testAddFriend() {
		userDbStorage.addFriend(1L, 2L);
		userDbStorage.addFriend(1L, 3L);
		Set<Long> userFriends = userDbStorage.getUser(1L).get().getFriends();
		Set<Long> user2Friends = userDbStorage.getUser(2L).get().getFriends();
		Set<Long> user3Friends = userDbStorage.getUser(3L).get().getFriends();
		assertThat(userFriends).isNotEmpty().hasSize(2);
		assertThat(user2Friends).isEmpty();
		assertThat(user3Friends).isEmpty();
	}

	@Test
	public void testRemoveFriend() {
		userDbStorage.addFriend(1L, 2L);
		userDbStorage.addFriend(1L, 3L);
		userDbStorage.deleteFriend(1L, 2L);
		Set<Long> userFriends = userDbStorage.getUser(1L).get().getFriends();
		assertThat(userFriends).isNotEmpty().hasSize(1);
		userDbStorage.deleteFriend(1L, 3L);
		Set<Long> userFriendsUpd = userDbStorage.getUser(1L).get().getFriends();
		assertThat(userFriendsUpd).isEmpty();
	}

	@Test
	public void testClearUsersData() {
		userDbStorage.clearData();
		Collection<User> allUsers = userDbStorage.getUsers();
		assertThat(allUsers).isEmpty();
	}

	@Test
	public void testFindFilmById() {
		Optional<Film> filmOptional = filmDbStorage.getFilm(1L);
		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("id", 1L));
	}

	@Test
	public void testFindAllFilms() {
		Collection<Film> films = filmDbStorage.getFilms();
		assertThat(films)
				.isNotEmpty()
				.hasSize(3)
				.map(Film::getId)
				.containsExactly(1L, 2L, 3L);
	}

	@Test
	public void testAddFilm() {
		Film testFilm = new Film();
		testFilm.setName("test");
		testFilm.setDescription("description");
		testFilm.setReleaseDate(LocalDate.of(1900, 12, 25));
		testFilm.setDuration(1000);
		testFilm.setMpa(new Mpa(1L, "G"));

		filmDbStorage.addFilm(testFilm);
		Optional<Film> filmOptional = filmDbStorage.getFilm(4L);

		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("id", 4L));
		Collection<Film> allFilms = filmDbStorage.getFilms();
		assertThat(allFilms).isNotEmpty().hasSize(4);
	}

	@Test
	public void testAddNullNameFilm() {
		Film testFilm = new Film();
		testFilm.setDescription("description");
		testFilm.setReleaseDate(LocalDate.of(1900, 12, 25));
		testFilm.setDuration(1000);
		testFilm.setMpa(new Mpa(1L, "G"));

		assertThatThrownBy(() -> filmDbStorage.addFilm(testFilm))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	public void testAddNotFoundMpaFilm() {
		Film testFilm = new Film();
		testFilm.setName("test");
		testFilm.setDescription("description");
		testFilm.setReleaseDate(LocalDate.of(1900, 12, 25));
		testFilm.setDuration(1000);
		testFilm.setMpa(new Mpa(10L, "G"));

		assertThatThrownBy(() -> filmDbStorage.addFilm(testFilm)).isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	public void testUpdateFilm() {
		Film testFilm = new Film(1L, "updated", "updated", LocalDate.of(1925, 12, 12), 1000, new Mpa(1L, "G"), new LinkedHashSet<>());
		filmDbStorage.updateFilm(testFilm);
		Optional<Film> filmOptional = filmDbStorage.getFilm(1L);
		assertThat(filmOptional).isPresent().hasValueSatisfying(film -> {
			assertThat(film).hasFieldOrPropertyWithValue("name", "updated");
			assertThat(film).hasFieldOrPropertyWithValue("description", "updated");
			assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1925, 12, 12));
		});
	}

	@Test
	public void testUpdateFilmWithGenres() {
		LinkedHashSet<FilmGenre> genres = new LinkedHashSet<>();
		genres.add(new FilmGenre(1L, "Комедия"));
		genres.add(new FilmGenre(2L, "Драма"));
		Film testFilm = new Film(1L, "updated", "updated", LocalDate.of(1925, 12, 12), 1000, new Mpa(1L, "G"), genres);

		filmDbStorage.updateFilm(testFilm);

		Optional<Film> filmOptional = filmDbStorage.getFilm(1L);
		assertThat(filmOptional).isPresent().hasValueSatisfying(film -> {
			assertThat(film.getGenres().size()).isEqualTo(2);
		});
	}

	@Test
	public void testLikeFilm() {
		filmDbStorage.addLikeFilm(1L, 1L);

		Optional<Film> filmOptional = filmDbStorage.getFilm(1L);

		assertThat(filmOptional).isPresent().hasValueSatisfying(film -> {
			assertThat(film.getUsersLikes()).hasSize(1);
		});
	}

	@Test
	public void testRemoveLikeFilm() {
		filmDbStorage.addLikeFilm(1L, 1L);
		filmDbStorage.addLikeFilm(1L, 2L);
		filmDbStorage.deleteLikeFilm(1L, 2L);

		Optional<Film> filmOptional = filmDbStorage.getFilm(1L);
		assertThat(filmOptional).isPresent().hasValueSatisfying(film -> {
			assertThat(film.getUsersLikes()).hasSize(1);
		});
	}

	@Test
	public void testClearFilmsData() {
		filmDbStorage.clearData();
		Collection<Film> allFilms = filmDbStorage.getFilms();
		assertThat(allFilms).isEmpty();
	}

	@Test
	public void testGetMpaById() {
		Optional<Mpa> mpaOptional = mpaDbStorage.getMpa(1L);
		assertThat(mpaOptional).isPresent().hasValueSatisfying(mpa -> {
			assertThat(mpa).hasFieldOrPropertyWithValue("id", 1L);
		});
	}

	@Test
	public void testGetAllMpas() {
		Collection<Mpa> mpas = mpaDbStorage.getAllRatings();
		assertThat(mpas).isNotEmpty().hasSize(5);
	}

	@Test
	public void testGetGenreById() {
		Optional<FilmGenre> genreOptional = genreDbStorage.getGenre(1L);
		assertThat(genreOptional).isPresent().hasValueSatisfying(genre -> {
			assertThat(genre).hasFieldOrPropertyWithValue("id", 1L);
		});
	}

	@Test
	public void testGetAllGenres() {
		Collection<FilmGenre> genres = genreDbStorage.getGenres();
		assertThat(genres).isNotEmpty().hasSize(6);
	}
 */
}