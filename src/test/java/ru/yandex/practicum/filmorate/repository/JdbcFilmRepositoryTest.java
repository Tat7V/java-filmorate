package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(JdbcFilmRepository.class)
@ActiveProfiles("test")
class JdbcFilmRepositoryTest {
    private final JdbcFilmRepository filmRepository;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setId(1L);
        testFilm.setName("Film One");
        testFilm.setDescription("Description One");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1L);
        mpa.setName("G");
        mpa.setDescription("Нет возрастных ограничений");
        testFilm.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Комедия");
        testFilm.setGenres(Set.of(genre));
    }

    @Test
    void testGetFilmById() {
        Film actual = filmRepository.getById(1L).orElseThrow();
        assertThat(actual).usingRecursiveComparison().isEqualTo(testFilm);
    }

    @Test
    void testGetFilmByNotExistsId() {
        assertThat(filmRepository.getById(999L)).isEmpty();
    }

    @Test
    void testGetAllFilms() {
        List<Film> films = filmRepository.getAll();
        assertThat(films).hasSize(2);
    }

    @Test
    void testUpdateFilm() {
        Film filmToUpdate = filmRepository.getById(1L).orElseThrow();
        filmToUpdate.setName("Updated Name");

        Genre newGenre = new Genre();
        newGenre.setId(2L);
        filmToUpdate.setGenres(Set.of(newGenre));

        Film updatedFilm = filmRepository.update(filmToUpdate);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Name");
        Film retrievedFilm = filmRepository.getById(1L).orElseThrow();
        assertThat(retrievedFilm.getGenres()).hasSize(1);
        assertThat(retrievedFilm.getGenres().iterator().next().getId()).isEqualTo(2L);
    }

    @Test
    void testAddLike() {
        filmRepository.addLike(2L, 2L);
        List<Film> popularFilms = filmRepository.getTopPopular(10);
        assertThat(popularFilms).hasSize(2);
    }

    @Test
    void testGetTopPopular() {
        List<Film> popularFilms = filmRepository.getTopPopular(10);
        assertThat(popularFilms.get(0).getId()).isEqualTo(1L);
        assertThat(popularFilms.get(1).getId()).isEqualTo(2L);
    }
}