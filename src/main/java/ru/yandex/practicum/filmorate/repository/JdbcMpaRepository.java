package ru.yandex.practicum.filmorate.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcMpaRepository implements MpaRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<Mpa> mpaRowMapper = (resultSet, rowNum) ->
            new Mpa(
                    resultSet.getLong("mpa_id"),
                    resultSet.getString("name"),
                    resultSet.getString("description")
            );

    public JdbcMpaRepository(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Collection<Mpa> getAll() {
        String sql = "SELECT * FROM mpa";
        return jdbc.query(sql, mpaRowMapper);
    }

    @Override
    public Optional<Mpa> getById(long id) {
        String sql = "SELECT * FROM mpa WHERE mpa_id = :id";
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(sql, Map.of("id", id), mpaRowMapper)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}