package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(JdbcUserRepository.class)
@ActiveProfiles("test")
class JdbcUserRepositoryTest {
    private final JdbcUserRepository userRepository;

    private User createTestUser(Long id, String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }

    @Test
    void testGetUserById() {
        User expected = createTestUser(1L, "user1@example.com", "user1", "User One",
                LocalDate.of(1990, 1, 1));

        User actual = userRepository.getById(1L).orElseThrow();

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void testGetUserByNotExistId() {
        assertThat(userRepository.getById(999L)).isEmpty();
    }

    @Test
    void testGetAllUsers() {
        List<User> users = userRepository.getAll();
        assertThat(users).hasSize(3);
    }

    @Test
    void testUpdateUserData() {
        User userToUpdate = userRepository.getById(1L).orElseThrow();
        userToUpdate.setName("Updated Name");

        User updatedUser = userRepository.update(userToUpdate);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(userRepository.getById(1L).orElseThrow().getName())
                .isEqualTo("Updated Name");
    }

    @Test
    void testGetUserFriends() {
        List<User> friends = userRepository.getFriends(1L);
        assertThat(friends).hasSize(1);

        User expectedFriend = createTestUser(2L, "user2@example.com", "user2",
                "User Two", LocalDate.of(1995, 5, 15));

        assertThat(friends.get(0)).usingRecursiveComparison().isEqualTo(expectedFriend);
    }

    @Test
    void testAddFriend() {
        userRepository.addFriend(1L, 3L);
        List<User> friends = userRepository.getFriends(1L);
        assertThat(friends).hasSize(2);
    }

    @Test
    void testRemoveFriend() {
        userRepository.deleteFriend(1L, 2L);
        assertThat(userRepository.getFriends(1L)).isEmpty();
    }
}