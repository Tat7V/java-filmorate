package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());

        user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));

        user3 = new User();
        user3.setEmail("user3@example.com");
        user3.setLogin("user3");
        user3.setBirthday(LocalDate.of(1992, 1, 1));
    }

    @Test
    void updateUser() {
        User created = userService.create(user1);
        created.setLogin("updatedLogin");

        User updated = userService.update(created);
        assertEquals("updatedLogin", updated.getLogin());
    }

    @Test
    void getAllUsers() {
        userService.create(user1);
        userService.create(user2);

        List<User> users = userService.getAll();
        assertEquals(2, users.size());
    }

    @Test
    void addAndRemoveFriend() {
        User userA = userService.create(user1);
        User userB = userService.create(user2);

        userService.addFriend(userA.getId(), userB.getId());

        List<User> friends = userService.getFriends(userA.getId());
        assertEquals(1, friends.size());
        assertEquals(userB.getId(), friends.get(0).getId());

        List<User> inverseFriends = userService.getFriends(userB.getId());
        assertEquals(1, inverseFriends.size());
        assertEquals(userA.getId(), inverseFriends.get(0).getId());

        userService.removeFriend(userA.getId(), userB.getId());
        friends = userService.getFriends(userA.getId());
        assertTrue(friends.isEmpty());
    }

    @Test
    void getCommonFriends() {
        User userA = userService.create(user1);
        User userB = userService.create(user2);
        User userC = userService.create(user3);

        userService.addFriend(userA.getId(), userB.getId());
        userService.addFriend(userA.getId(), userC.getId());
        userService.addFriend(userB.getId(), userC.getId());

        List<User> commonFriends = userService.getCommonFriends(userA.getId(), userB.getId());
        assertEquals(1, commonFriends.size());
        assertEquals(userC.getId(), commonFriends.get(0).getId());
    }
}