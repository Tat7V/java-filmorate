package ru.yandex.practicum.filmorate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Autowired
    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Film> filmRowMapper = (resultSet, rowNum) -> {
        Film film = new Film();
        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        Long mpaId = resultSet.getLong("mpa_id");
        if (!resultSet.wasNull()) {
            film.setMpa(new Mpa(mpaId, resultSet.getString("mpa_name"), resultSet.getString("mpa_description")));
        }

        return film;
    };

    private final ResultSetExtractor<Film> filmExtractor = resultSet -> {
        Film film = null;
        Set<Genre> genres = new HashSet<>();

        while (resultSet.next()) {
            if (film == null) {
                film = filmRowMapper.mapRow(resultSet, 0);
            }

            long genreId = resultSet.getLong("genre_id");
            if (!resultSet.wasNull()) {
                genres.add(new Genre(genreId, resultSet.getString("genre_name")));
            }
        }

        if (film != null) {
            film.setGenres(genres);
        }

        return film;
    };

    @Override
    public Optional<Film> getById(long id) {
        String sql = "SELECT f.*, m.name AS mpa_name, m.description AS mpa_description, " +
                "g.genre_id, g.name AS genre_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE f.film_id = :id";

        Map<String, Object> params = Map.of("id", id);
        Film film = jdbc.query(sql, params, filmExtractor);
        return Optional.ofNullable(film);
    }

    @Override
    public List<Film> getAll() {
        // фильмы без жанров
        String filmsSql = "SELECT f.*, m.name AS mpa_name, m.description AS mpa_description FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id";
        List<Film> films = jdbc.query(filmsSql, filmRowMapper);

        if (!films.isEmpty()) {
            //связи фильм-жанр
            String genresSql = "SELECT fg.film_id, g.genre_id, g.name AS genre_name " +
                    "FROM film_genres fg JOIN genres g ON fg.genre_id = g.genre_id " +
                    "WHERE fg.film_id IN (:filmIds)";

            Map<Long, Set<Genre>> filmGenres = new HashMap<>();
            List<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());

            jdbc.query(genresSql, Map.of("filmIds", filmIds), rs -> {
                long filmId = rs.getLong("film_id");
                Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
                filmGenres.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
            });
            // жанры для каждого фильма
            films.forEach(f -> f.setGenres(filmGenres.getOrDefault(f.getId(), Collections.emptySet())));
        }
        return films;
    }

    @Override
    public Film save(Film film) {
        if (film.getId() == null) {
            return create(film);
        } else {
            return update(film);
        }
    }

    private Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (:name, :description, :releaseDate, :duration, :mpaId)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", film.getMpa() != null ? film.getMpa().getId() : null);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"film_id"});

        long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(filmId);

        updateFilmGenres(film);
        return film;
    }

    Film update(Film film) {
        String sql = "UPDATE films SET name = :name, description = :description, " +
                "release_date = :releaseDate, duration = :duration, mpa_id = :mpaId " +
                "WHERE film_id = :filmId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", film.getId())
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", film.getMpa() != null ? film.getMpa().getId() : null);

        jdbc.update(sql, params);
        updateFilmGenres(film);

        return film;
    }

    private void updateFilmGenres(Film film) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = :filmId",
                Map.of("filmId", film.getId()));

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (:filmId, :genreId)";

            List<MapSqlParameterSource> batchParams = film.getGenres().stream()
                    .map(genre -> new MapSqlParameterSource()
                            .addValue("filmId", film.getId())
                            .addValue("genreId", genre.getId()))
                    .collect(Collectors.toList());

            jdbc.batchUpdate(sql, batchParams.toArray(new MapSqlParameterSource[0]));
        }
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (:filmId, :userId)";
        jdbc.update(sql, Map.of("filmId", filmId, "userId", userId));
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        String sql = "DELETE FROM likes WHERE film_id = :filmId AND user_id = :userId";
        jdbc.update(sql, Map.of("filmId", filmId, "userId", userId));
    }

    @Override
    public List<Film> getTopPopular(int count) {
        String sql = "SELECT f.*, m.name AS mpa_name, m.description AS mpa_description, " +
                "COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id, m.mpa_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT :count";

        List<Film> films = jdbc.query(sql, Map.of("count", count), filmRowMapper);

        if (!films.isEmpty()) {
            String genresSql = "SELECT fg.film_id, g.genre_id, g.name AS genre_name " +
                    "FROM film_genres fg JOIN genres g ON fg.genre_id = g.genre_id " +
                    "WHERE fg.film_id IN (:filmIds)";

            Map<Long, Set<Genre>> filmGenres = new HashMap<>();
            List<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());

            jdbc.query(genresSql, Map.of("filmIds", filmIds), resultSet -> {
                long filmId = resultSet.getLong("film_id");
                Genre genre = new Genre(resultSet.getLong("genre_id"), resultSet.getString("genre_name"));
                filmGenres.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
            });

            films.forEach(film -> film.setGenres(filmGenres.getOrDefault(film.getId(), Collections.emptySet())));
        }
        return films;
    }
}