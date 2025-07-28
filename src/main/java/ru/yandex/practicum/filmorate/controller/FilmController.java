package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private static final String LIKE_PATH = "/{id}/like/{userId}";
    private static final String GENRE_PATH = "/{id}/genre/";
    private static final String MPA_PATH = "/{id}/mpa/";

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id) {
        return filmService.getById(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (!film.isValidReleaseDate()) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping(LIKE_PATH)
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(LIKE_PATH)
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(
            @RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
    @PostMapping(GENRE_PATH)
    public void addGenre(@PathVariable Long id, @RequestBody Genre genre) {
        filmService.addGenreToFilm(id, genre);
    }

    @DeleteMapping(GENRE_PATH)
    public void removeGenre(@PathVariable Long id, @RequestBody Genre genre) {
        filmService.removeGenreFromFilm(id, genre);
    }

    @PostMapping(MPA_PATH)
    public void setMpa(@PathVariable Long id, @RequestBody Mpa mpa) {
        filmService.setMpa(id, mpa);
    }

    @DeleteMapping(MPA_PATH)
    public void removeMpa(@PathVariable Long id) {
        filmService.removeMpa(id);
    }
}