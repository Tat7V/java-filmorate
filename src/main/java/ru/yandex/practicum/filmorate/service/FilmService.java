package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        if (!film.isValidReleaseDate()) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (!filmStorage.existsById(film.getId())) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(Long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getById(filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (!likes.containsKey(filmId)) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден среди лайков");
        }
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        Set<Long> filmLikes = likes.get(filmId);
        if (!filmLikes.contains(userId)) {
            throw new NotFoundException("Лайк пользователя с id=" + userId + " не найден");
        }

        filmLikes.remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted((film1, film2) -> {
                    int likes1 = likes.getOrDefault(film1.getId(), Collections.emptySet()).size();
                    int likes2 = likes.getOrDefault(film2.getId(), Collections.emptySet()).size();
                    return Integer.compare(likes2, likes1);
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}