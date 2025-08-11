package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(JdbcMpaRepository.class)
class JdbcMpaRepositoryTest {
    private final JdbcMpaRepository mpaRepository;

    private Mpa getTestMpa() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        mpa.setName("G");
        mpa.setDescription("Нет возрастных ограничений");
        return mpa;
    }

    @Test
    void testGetMpaById() {
        Mpa expected = getTestMpa();
        Mpa actual = mpaRepository.getById(1L).orElseThrow();

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void testGetMpaByNotExistId() {
        assertThat(mpaRepository.getById(999L)).isEmpty();
    }

    @Test
    void testGetAllMpa() {
        Collection<Mpa> mpaList = mpaRepository.getAll();
        assertThat(mpaList).hasSize(5);
    }
}