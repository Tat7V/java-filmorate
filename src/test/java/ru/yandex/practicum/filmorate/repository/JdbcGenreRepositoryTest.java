package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(JdbcGenreRepository.class)
class JdbcGenreRepositoryTest {
    private final JdbcGenreRepository genreRepository;

    private Genre getTestGenre() {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Комедия");
        return genre;
    }

    @Test
    void testGetGenreById() {
        Genre expected = getTestGenre();
        Genre actual = genreRepository.getById(1L).orElseThrow();

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void testGetGenreByNotExistsId() {
        assertThat(genreRepository.getById(999L)).isEmpty();
    }

    @Test
    void testGetAllGenres() {
        Collection<Genre> genres = genreRepository.getAll();
        assertThat(genres).hasSize(6);
    }

    @Test
    void testGetGenresByIds() {
        Collection<Genre> genres = genreRepository.getByIds(List.of(1L, 2L));
        assertThat(genres).hasSize(2);
    }
}