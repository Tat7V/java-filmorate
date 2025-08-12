package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.Collection;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Collection<Genre> getAll() {
        return genreRepository.getAll();
    }

    public Genre getById(long id) {
        return genreRepository.getById(id).orElseThrow(() -> new NotFoundException(String.format("Жанр с id=%d не найден", id)));
    }

    public Collection<Genre> getByIds(Collection<Long> ids) {
        return genreRepository.getByIds(ids);
    }
}