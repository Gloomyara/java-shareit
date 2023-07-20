package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private RequestRepository requestRepository;

    private final User user = User.builder()
            .name("test_user1")
            .email("test1@test.omg")
            .build();

    private final User user2 = User.builder()
            .name("test_user2")
            .email("test2@test.omg")
            .build();

    private final Item item = Item.builder()
            .name("test_item1")
            .description("test_description1")
            .available(true)
            .build();

    private final Request request = Request.builder()
            .description("test_description")
            .created(LocalDateTime.now())
            .author(user)
            .build();

    @BeforeEach
    void setUp() {
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(user2);
        item.setOwner(user);
        entityManager.persistAndFlush(item);
    }

    @Test
    void findByIdWithItems() {
        List<Item> requestItems = List.of(item);
        request.setItems(requestItems);
        entityManager.persistAndFlush(request);
        Optional<Request> foundRequest = requestRepository.findByIdWithItems(request.getId());
        assertThat(foundRequest).contains(request);
        assertThat(foundRequest.get().getItems().get(0)).isEqualTo(requestItems.get(0));
    }

    @Test
    void findAllByAuthorIdWithItems() {
        request.setItems(List.of(item));
        entityManager.persistAndFlush(request);
        List<Request> foundRequests = requestRepository.findAllByAuthorIdWithItems(user.getId());
        assertThat(foundRequests).hasSize(1);
        assertThat(foundRequests.get(0)).isEqualTo(request);
        assertThat(foundRequests.get(0).getItems()).hasSize(1);
        assertThat(foundRequests.get(0).getItems().get(0)).isEqualTo(item);
    }

    @Test
    void findAllOtherRequestsByUserId() {
        request.setAuthor(user);
        entityManager.persistAndFlush(request);

        List<Request> foundRequests1 = requestRepository
                .findAllOtherRequests(user.getId(), Pageable.ofSize(10)).toList();
        assertThat(foundRequests1).isEmpty();

        List<Request> foundRequests2 = requestRepository
                .findAllOtherRequests(user2.getId(), Pageable.ofSize(10)).toList();
        assertThat(foundRequests2).hasSize(1);
    }
}
