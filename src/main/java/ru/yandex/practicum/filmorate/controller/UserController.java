package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private static final String ID_PATH = "/{id}";
    private static final String FRIENDS_PATH = ID_PATH + "/friends";
    private static final String FRIEND_ID_PATH = FRIENDS_PATH + "/{friendId}";
    private static final String COMMON_FRIENDS_ID_PATH = FRIENDS_PATH + "/common/{otherId}";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping(ID_PATH)
    public User getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping(FRIEND_ID_PATH)
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(FRIEND_ID_PATH)
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping(FRIENDS_PATH)
    public List<User> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping(COMMON_FRIENDS_ID_PATH)
    public List<User> getCommonFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}