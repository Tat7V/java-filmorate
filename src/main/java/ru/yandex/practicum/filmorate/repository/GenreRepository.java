package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreRepository {
    Collection<Genre> getAll();

    Optional<Genre> getById(long id);

    Collection<Genre> getByIds(Collection<Long> ids);
}