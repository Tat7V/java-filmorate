package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(User user) throws NotFoundException;

    List<User> getAll();

    User getById(Long id) throws NotFoundException;

    void addFriend(Long userId, Long friendId) throws NotFoundException;

    void removeFriend(Long userId, Long friendId) throws NotFoundException;

    List<User> getFriends(Long userId) throws NotFoundException;

    List<User> getCommonFriends(Long userId, Long otherId) throws NotFoundException;
}