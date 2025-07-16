package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();
    private long idCounter = 1;

    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    @Override
    public User create(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!existsById(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}