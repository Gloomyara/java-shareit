package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    private final User user1 = User.builder()
            .name("test_user1")
            .email("test1@test.omg")
            .build();

    private final User user2 = User.builder()
            .name("test_user2")
            .email("test2@test.omg")
            .build();

    @BeforeEach
    void setUp() {
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
    }

    @Test
    void existByEmailAndIdNot_assertTrue() {
        boolean b = userRepository.existsByEmailAndIdNot(user1.getEmail(), user2.getId());
        assertTrue(b);
    }

    @Test
    void existByEmailAndIdNot_assertFalse() {
        boolean b = userRepository.existsByEmailAndIdNot("qwerty@qwerty322.com", user1.getId());
        assertFalse(b);
    }
}
