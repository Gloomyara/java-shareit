package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;

    private final User user = User.builder()
            .name("test_user")
            .email("test@test.omg")
            .build();

    private final Item item = Item.builder()
            .name("test_item1")
            .description("test_description1")
            .available(true)
            .owner(user)
            .build();

    @BeforeEach
    void setUp() {
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(item);
    }

    @Test
    void findByIdWithOwner() {
        Optional<Item> foundItem = itemRepository.findByIdWithOwner(item.getId());
        assertThat(foundItem).isPresent();
    }

    @Test
    void findByIdWithOwnerAndComments() {
        Optional<Item> foundItem = itemRepository.findByIdWithOwnerAndComments(item.getId());
        assertThat(foundItem).isPresent();
    }

    @Test
    void findAllByOwnerIdWithComments() {
        Page<Item> foundItems = itemRepository.findAllByOwnerIdWithComments(user.getId(), Pageable.ofSize(10));
        assertThat(foundItems.toList()).hasSize(1);
    }

    @Test
    void existsByIdAndOwnerId_assertTrue() {
        assertTrue(itemRepository.existsByIdAndOwnerId(item.getId(), user.getId()));
    }

    @Test
    void existsByIdAndUserId_assertFalse() {
        assertFalse(itemRepository.existsByIdAndOwnerId(item.getId(), 999L));
    }
}
