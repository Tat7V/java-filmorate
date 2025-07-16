package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    private FilmService filmService;
    private InMemoryUserStorage userStorage;
    private Film film1;
    private Film film2;
    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(
                new InMemoryFilmStorage(),
                userStorage
        );

        testUser1 = new User();
        testUser1.setEmail("user1@example.com");
        testUser1.setLogin("user1");
        testUser1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.create(testUser1);

        testUser2 = new User();
        testUser2.setEmail("user2@example.com");
        testUser2.setLogin("user2");
        testUser2.setBirthday(LocalDate.of(1991, 1, 1));
        userStorage.create(testUser2);

        // Создаем тестовые фильмы
        film1 = new Film();
        film1.setName("Фильм 1");
        film1.setDescription("Описание 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);

        film2 = new Film();
        film2.setName("Фильм 2");
        film2.setDescription("Описание 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(130);
    }

    @Test
    void updateFilm() {
        Film created = filmService.create(film1);
        created.setName("Новое название");

        Film updated = filmService.update(created);
        assertEquals("Новое название", updated.getName());
    }

    @Test
    void updateNonExistentFilm() {
        film1.setId(999L);
        assertThrows(NotFoundException.class, () -> filmService.update(film1));
    }

    @Test
    void getAllFilms() {
        filmService.create(film1);
        filmService.create(film2);

        List<Film> films = filmService.getAll();
        assertEquals(2, films.size());
    }

    @Test
    void addAndRemoveLike() {
        Film film = filmService.create(film1);
        filmService.addLike(film.getId(), testUser1.getId()); // Используем существующего пользователя
        filmService.addLike(film.getId(), testUser2.getId());

        List<Film> popular = filmService.getPopularFilms(1);
        assertEquals(1, popular.size());
        assertEquals(film.getId(), popular.get(0).getId());

        filmService.removeLike(film.getId(), testUser1.getId());
        popular = filmService.getPopularFilms(1);
        assertEquals(film.getId(), popular.get(0).getId());
    }

    @Test
    void getPopularFilms() {
        Film filmA = filmService.create(film1);
        Film filmB = filmService.create(film2);

        filmService.addLike(filmA.getId(), 1L);
        filmService.addLike(filmA.getId(), 2L);
        filmService.addLike(filmB.getId(), 1L);

        List<Film> popular = filmService.getPopularFilms(2);
        assertEquals(2, popular.size());
        assertEquals(filmA.getId(), popular.get(0).getId());
        assertEquals(filmB.getId(), popular.get(1).getId());

        popular = filmService.getPopularFilms(1);
        assertEquals(1, popular.size());
        assertEquals(filmA.getId(), popular.get(0).getId());
    }
}