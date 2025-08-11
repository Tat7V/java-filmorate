package ru.yandex.practicum.filmorate.repository;


import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaRepository {
    Collection<Mpa> getAll();

    Optional<Mpa> getById(long id);
}
