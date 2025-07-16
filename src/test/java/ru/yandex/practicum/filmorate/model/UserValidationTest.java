package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validateEmptyEmail() {
        User user = new User();
        user.setEmail("");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Email не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void validateInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Некорректный формат email", violations.iterator().next().getMessage());
    }

    @Test
    void validateEmptyLogin() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setLogin(" ");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.size());

        // Проверяем сообщения обеих ошибок
        List<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        assertTrue(messages.contains("Логин не может быть пустым"));
        assertTrue(messages.contains("Логин не может содержать пробелы"));
    }


    @Test
    void validateFutureBirthday() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }
}