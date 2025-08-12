package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getById(long id);

    List<User> getAll();

    User save(User user);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    User update(User user);

    List<User> getFriends(long userId);

    List<User> getCommonFriends(long userId, long otherId);

    boolean isFriend(long userId, long friendId);
}