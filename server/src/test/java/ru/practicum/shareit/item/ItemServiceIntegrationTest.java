package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemServiceImpl itemService;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void create_whenRequestIdNull() {
        User owner = userRepository.save(generator.nextObject(User.class));
        ItemDtoIn dtoIn = generator.nextObject(ItemDtoIn.class);
        dtoIn.setRequestId(null);
        ItemDtoOut dtoOut = itemService.save(dtoIn, owner.getId());
        assertNotNull(dtoOut.getId());
        assertEquals(dtoIn.getName(), dtoOut.getName());
        assertEquals(dtoIn.getDescription(), dtoOut.getDescription());
        assertEquals(dtoIn.getAvailable(), dtoOut.getAvailable());
        assertNull(dtoOut.getLastBooking());
        assertNull(dtoOut.getNextBooking());
        assertNull(dtoOut.getComments());
        assertNull(dtoOut.getRequestId());
    }

    @Test
    void create_whenRequestIdNotNull() {
        User owner = userRepository.save(generator.nextObject(User.class));
        User author = userRepository.save(generator.nextObject(User.class));
        Request request = generator.nextObject(Request.class);
        request.setAuthor(author);
        request.setItems(null);
        Request requestFromDb = requestRepository.save(request);
        ItemDtoIn dtoIn = generator.nextObject(ItemDtoIn.class);
        dtoIn.setRequestId(requestFromDb.getId());
        ItemDtoOut dtoOut = itemService.save(dtoIn, owner.getId());
        assertNotNull(dtoOut.getId());
        assertEquals(dtoIn.getName(), dtoOut.getName());
        assertEquals(dtoIn.getDescription(), dtoOut.getDescription());
        assertEquals(dtoIn.getAvailable(), dtoOut.getAvailable());
        assertNull(dtoOut.getLastBooking());
        assertNull(dtoOut.getNextBooking());
        assertNull(dtoOut.getComments());
        assertEquals(dtoIn.getRequestId(), dtoOut.getRequestId());
    }

    @Test
    void findById_whenUserItemOwner() {
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = generator.nextObject(Item.class);
        item.setOwner(owner);
        item.setRequest(null);
        item.setComments(null);
        item = itemRepository.save(item);
        Booking nextBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        Booking lastBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(nextBooking);
        bookingRepository.save(lastBooking);
        ItemDtoOut dtoOut = itemService.findById(item.getId(), owner.getId());
        assertEquals(item.getId(), dtoOut.getId());
        assertEquals(item.getName(), dtoOut.getName());
        assertEquals(item.getDescription(), dtoOut.getDescription());
        assertEquals(item.getAvailable(), dtoOut.getAvailable());
        assertEquals(lastBooking.getId(), dtoOut.getLastBooking().getId());
        assertEquals(lastBooking.getBooker().getId(), dtoOut.getLastBooking().getBookerId());
        assertEquals(nextBooking.getId(), dtoOut.getNextBooking().getId());
        assertEquals(nextBooking.getBooker().getId(), dtoOut.getNextBooking().getBookerId());
        assertNull(dtoOut.getComments());
    }

    @Test
    void findById_whenUserNotItemOwner() {
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = generator.nextObject(Item.class);
        item.setOwner(owner);
        item.setRequest(null);
        item.setComments(null);
        item = itemRepository.save(item);
        Booking nextBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        Booking lastBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().minusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(nextBooking);
        bookingRepository.save(lastBooking);
        ItemDtoOut dtoOut = itemService.findById(item.getId(), booker.getId());
        assertEquals(item.getId(), dtoOut.getId());
        assertEquals(item.getName(), dtoOut.getName());
        assertEquals(item.getDescription(), dtoOut.getDescription());
        assertEquals(item.getAvailable(), dtoOut.getAvailable());
        assertNull(dtoOut.getLastBooking());
        assertNull(dtoOut.getLastBooking());
        assertNull(dtoOut.getNextBooking());
        assertNull(dtoOut.getNextBooking());
        assertNull(dtoOut.getComments());
    }

    @Test
    void findAllByOwnerId() {
        Integer from = 0;
        Integer limit = 10;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = generator.nextObject(Item.class);
        item.setOwner(owner);
        item.setRequest(null);
        item.setComments(null);
        item = itemRepository.save(item);
        Booking nextBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        Booking lastBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(nextBooking);
        bookingRepository.save(lastBooking);
        List<ItemDtoOut> foundItems = itemService.findAllByOwnerId(from, limit, owner.getId());
        assertThat(foundItems).hasSize(1);
        ItemDtoOut dtoOut = foundItems.get(0);
        assertEquals(item.getId(), dtoOut.getId());
        assertEquals(item.getName(), dtoOut.getName());
        assertEquals(item.getDescription(), dtoOut.getDescription());
        assertEquals(item.getAvailable(), dtoOut.getAvailable());
        assertEquals(lastBooking.getId(), dtoOut.getLastBooking().getId());
        assertEquals(lastBooking.getBooker().getId(), dtoOut.getLastBooking().getBookerId());
        assertEquals(nextBooking.getId(), dtoOut.getNextBooking().getId());
        assertEquals(nextBooking.getBooker().getId(), dtoOut.getNextBooking().getBookerId());
        assertNull(dtoOut.getComments());
    }

    @Test
    void searchByName() {
        int from = 0;
        int size = 10;
        User owner = userRepository.save(generator.nextObject(User.class));
        Item item = generator.nextObject(Item.class);
        item.setOwner(owner);
        item.setRequest(null);
        item.setComments(null);
        item = itemRepository.save(item);
        List<ItemDtoOut> foundItems = itemService.searchByNameOrDescription(from, size, item.getName());
        assertThat(foundItems).hasSize(1);
        ItemDtoOut dtoOut = foundItems.get(0);
        assertEquals(item.getId(), dtoOut.getId());
        assertEquals(item.getName(), dtoOut.getName());
        assertEquals(item.getDescription(), dtoOut.getDescription());
        assertEquals(item.getAvailable(), dtoOut.getAvailable());
    }

    @Test
    void searchByDescription() {
        int from = 0;
        int size = 10;
        User owner = userRepository.save(generator.nextObject(User.class));
        Item item = generator.nextObject(Item.class);
        item.setOwner(owner);
        item.setRequest(null);
        item.setComments(null);
        item = itemRepository.save(item);
        List<ItemDtoOut> foundItems = itemService.searchByNameOrDescription(from, size, item.getDescription());
        assertThat(foundItems).hasSize(1);
        ItemDtoOut dtoOut = foundItems.get(0);
        assertEquals(item.getId(), dtoOut.getId());
        assertEquals(item.getName(), dtoOut.getName());
        assertEquals(item.getDescription(), dtoOut.getDescription());
        assertEquals(item.getAvailable(), dtoOut.getAvailable());
    }

    @Test
    void createComment() {
        User commentator = userRepository.save(generator.nextObject(User.class));
        User owner = userRepository.save(generator.nextObject(User.class));
        Item item = generator.nextObject(Item.class);
        item.setOwner(owner);
        item.setRequest(null);
        item.setComments(List.of());
        item = itemRepository.save(item);
        Item finalItem = item;
        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(commentator)
                .status(Status.APPROVED)
                .build());
        CommentDtoOut created = itemService.createComment(finalItem.getId(),
                commentator.getId(), generator.nextObject(CommentDtoIn.class));
        assertNotNull(created.getId());
        assertEquals(commentator.getName(), created.getAuthorName());
        assertNotNull(created.getText());
        assertNotNull(created.getCreated());
    }
}
