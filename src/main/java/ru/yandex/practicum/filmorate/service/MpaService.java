package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaRepository mpaRepository;

    public Collection<Mpa> getAll() {
        return mpaRepository.getAll();
    }

    public Mpa getById(long id) {
        return mpaRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Рейтинг с id=%d не найден", id)));
    }
}