package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void shouldAddValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(2001, 1, 1));

        User addedUser = userController.create(user);
        assertNotNull(addedUser.getId());
        assertEquals(1, userController.findAll().size());
    }

    @Test
    void shouldUseLoginWhenNameIsEmpty() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(2001, 1, 1));
        user.setName("");

        User addedUser = userController.create(user);
        assertEquals("testlogin", addedUser.getName());
    }

    @Test
    void shouldUpdateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User addedUser = userController.create(user);

        addedUser.setEmail("updated@example.com");
        User updatedUser = userController.update(addedUser);

        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals(1, userController.findAll().size());
    }

    @Test
    void shouldFailUpdateWhenUserNotExist() {
        User user = new User();
        user.setId(999L);
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(2001, 1, 1));

        assertThrows(ValidationException.class, () -> userController.update(user));
    }
}