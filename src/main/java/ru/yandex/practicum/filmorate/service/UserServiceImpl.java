package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        getById(user.getId());
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User getById(Long id) {
        return userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        getById(userId);
        getById(friendId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователь не может добавить самого себя в друзья");
        }
        if (userRepository.isFriend(userId, friendId)) {
            throw new ValidationException("Пользователи уже являются друзьями");
        }
        userRepository.addFriend(userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        getById(userId);
        getById(friendId);
        userRepository.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        getById(userId);
        return userRepository.getFriends(userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        getById(userId);
        getById(otherId);
        return userRepository.getCommonFriends(userId, otherId);
    }
}