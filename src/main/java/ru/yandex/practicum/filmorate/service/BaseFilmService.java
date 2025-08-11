package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BaseFilmService implements FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;

    @Autowired
    public BaseFilmService(FilmRepository filmRepository,
                           UserRepository userRepository,
                           MpaRepository mpaRepository,
                           GenreRepository genreRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.mpaRepository = mpaRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public Film create(Film film) {
        validateFilm(film);
        return filmRepository.save(film);
    }

    @Override
    public Film update(Film film) {
        validateFilm(film);
        if (filmRepository.getById(film.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        return filmRepository.save(film);
    }

    @Override
    public List<Film> getAll() {
        return filmRepository.getAll();
    }

    @Override
    public Film getById(Long id) {
        return filmRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        getById(filmId);
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        filmRepository.addLike(filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        getById(filmId);
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        filmRepository.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmRepository.getTopPopular(count);
    }

    private void validateFilm(Film film) {
        if (!film.isValidReleaseDate()) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getMpa() != null) {
            mpaRepository.getById(film.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("MPA с id=" + film.getMpa().getId() + " не существует"));
        } else {
            throw new ValidationException("Фильм должен иметь возрастной рейтинг (MPA)");
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Long> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            Set<Long> existingIds = genreRepository.getByIds(genreIds).stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            if (!existingIds.containsAll(genreIds)) {
                throw new NotFoundException("Указаны несуществующие жанры");
            }
        }
    }
}
