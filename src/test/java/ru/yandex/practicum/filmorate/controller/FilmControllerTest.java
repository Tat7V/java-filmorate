package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void shouldAddValidFilm() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film addedFilm = filmController.create(film);
        assertNotNull(addedFilm.getId());
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    void shouldUpdateFilm() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Film addedFilm = filmController.create(film);

        addedFilm.setName("Обновленное название фильма");
        Film updatedFilm = filmController.update(addedFilm);

        assertEquals("Обновленное название фильма", updatedFilm.getName());
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    void shouldFailReleaseTooEarly() {
        Film invalidFilm = new Film();
        invalidFilm.setName("Название");
        invalidFilm.setDescription("Описание");
        invalidFilm.setDuration(120);
        invalidFilm.setReleaseDate(LocalDate.of(1890, 12, 27)); // Невалидная дата

        Exception exception = assertThrows(ValidationException.class,
                () -> filmController.create(invalidFilm)
        );

        assertTrue(exception.getMessage().contains("Дата релиза не может быть раньше 28 декабря 1895 года"));
    }
}
