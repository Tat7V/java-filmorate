package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;

public interface FilmService {
    Film create(Film film) throws ValidationException;

    Film update(Film film) throws NotFoundException, ValidationException;

    List<Film> getAll();

    Film getById(Long id) throws NotFoundException;

    void addLike(Long filmId, Long userId) throws NotFoundException;

    void removeLike(Long filmId, Long userId) throws NotFoundException;

    List<Film> getPopularFilms(int count);
}