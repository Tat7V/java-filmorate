package ru.yandex.practicum.filmorate.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<Genre> genreRowMapper = (resultSet, rowNum) ->
            new Genre(
                    resultSet.getLong("genre_id"),
                    resultSet.getString("name")
            );

    public JdbcGenreRepository(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Collection<Genre> getAll() {
        String sql = "SELECT * FROM genres ORDER BY genre_id";
        return jdbc.query(sql, genreRowMapper);
    }

    @Override
    public Optional<Genre> getById(long id) {
        String sql = "SELECT * FROM genres WHERE genre_id = :id";
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(sql, Map.of("id", id), genreRowMapper)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Genre> getByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        String sql = "SELECT * FROM genres WHERE genre_id IN (:ids)";
        return jdbc.query(sql, Map.of("ids", ids), genreRowMapper);
    }
}