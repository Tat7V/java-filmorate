package ru.yandex.practicum.filmorate.repository;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Autowired
    public JdbcUserRepository(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<User> userRowMapper = (resultSet, rowNum) -> {
        User user = new User();
        user.setId(resultSet.getLong("user_id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());
        return user;
    };

    @Override
    public Optional<User> getById(long id) {
        String sql = "SELECT * FROM users WHERE user_id = :id";
        List<User> users = jdbc.query(sql, Map.of("id", id), userRowMapper);
        return users.stream().findFirst();
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, userRowMapper);
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            return create(user);
        } else {
            return update(user);
        }
    }

    private User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (:email, :login, :name, :birthday)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"user_id"});

        long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(userId);

        return user;
    }

    public User update(User user) {
        String sql = "UPDATE users SET email = :email, login = :login, " +
                "name = :name, birthday = :birthday " +
                "WHERE user_id = :userId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", user.getId())
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());

        jdbc.update(sql, params);
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sql = "INSERT INTO friendship (user_id, friend_id) VALUES (:userId, :friendId)";
        jdbc.update(sql, Map.of("userId", userId, "friendId", friendId));
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = :userId AND friend_id = :friendId";
        jdbc.update(sql, Map.of("userId", userId, "friendId", friendId));
    }

    @Override
    public List<User> getFriends(long userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendship f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = :userId";
        return jdbc.query(sql, Map.of("userId", userId), userRowMapper);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendship f1 ON u.user_id = f1.friend_id " +
                "JOIN friendship f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = :userId AND f2.user_id = :otherId";
        return jdbc.query(sql, Map.of("userId", userId, "otherId", otherId), userRowMapper);
    }

    @Override
    public boolean isFriend(long userId, long friendId) {
        String sql = "SELECT COUNT(*) FROM friendship WHERE user_id = :userId AND friend_id = :friendId";
        Integer count = jdbc.queryForObject(sql, Map.of("userId", userId, "friendId", friendId), Integer.class);
        return count != null && count > 0;
    }
}