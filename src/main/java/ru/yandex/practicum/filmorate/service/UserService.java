package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> friends = new HashMap<>();
    private final Map<Long, Map<Long, FriendshipStatus>> friendshipStatuses = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        if (!userStorage.existsById(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        return userStorage.update(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(Long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);

        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friendshipStatuses.computeIfAbsent(userId, k -> new HashMap<>())
                .put(friendId, new FriendshipStatus(1, "Неподтверждённая"));
    }

    public void confirmFriend(Long userId, Long friendId) {
        if (!friends.containsKey(userId) || !friends.get(userId).contains(friendId)) {
            throw new NotFoundException("Запрос на дружбу не найден");
        }

        friendshipStatuses.get(userId).put(friendId, new FriendshipStatus(2, "Подтверждённая"));
        friendshipStatuses.computeIfAbsent(friendId, k -> new HashMap<>())
                .put(userId, new FriendshipStatus(2, "Подтверждённая"));
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);

        if (friends.containsKey(userId)) {
            friends.get(userId).remove(friendId);
            friendshipStatuses.get(userId).remove(friendId);
        }
        if (friends.containsKey(friendId)) {
            friends.get(friendId).remove(userId);
            friendshipStatuses.get(friendId).remove(userId);
        }
    }

    public List<User> getFriends(Long userId) {
        getById(userId);  // Проверяем существование пользователя

        return friends.getOrDefault(userId, Collections.emptySet())
                .stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = getById(userId);
        User otherUser = getById(otherId);

        Set<Long> userFriends = friends.getOrDefault(userId, Collections.emptySet());
        Set<Long> otherFriends = friends.getOrDefault(otherId, Collections.emptySet());

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public FriendshipStatus getFriendshipStatus(Long userId, Long friendId) {
        if (!friendshipStatuses.containsKey(userId) || !friendshipStatuses.get(userId).containsKey(friendId)) {
            throw new NotFoundException("Статус дружбы не найден");
        }
        return friendshipStatuses.get(userId).get(friendId);
    }
}
