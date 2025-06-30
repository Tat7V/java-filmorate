package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {
    private Validator validator;
    private Film validFilm;
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validFilm = new Film();
        validFilm.setName("Название фильма");
        validFilm.setDescription("Описание");
        validFilm.setReleaseDate(LocalDate.of(2011, 1, 1));
        validFilm.setDuration(120);
    }

    @Test
    void shouldPassValidFilm() {
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailNameIsBlank() {
        validFilm.setName(" ");
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailDescriptionTooLong() {
        validFilm.setDescription("a".repeat(201));
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertEquals(1, violations.size());
        assertEquals("Описание не должно превышать 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailNegativeDuration() {
        validFilm.setDuration(-1);
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertEquals(1, violations.size());
        assertEquals("Продолжительность должна быть положительной", violations.iterator().next().getMessage());
    }
}
