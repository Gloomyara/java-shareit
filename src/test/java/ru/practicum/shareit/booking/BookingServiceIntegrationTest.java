package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.util.UtilConstants.*;

@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingServiceImpl bookingService;

    private final EasyRandom generator = new EasyRandom();
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    private final int from = Integer.parseInt(DEFAULT_FROM);
    private final int limit = Integer.parseInt(DEFAULT_LIMIT);

    @Test
    void create() {
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        BookingDtoIn dtoIn = generator.nextObject(BookingDtoIn.class);
        dtoIn.setEnd(LocalDateTime.MAX);
        dtoIn.setItemId(item.getId());
        BookingDtoOut dtoOut = bookingService.create(dtoIn, booker.getId());
        assertNotNull(dtoOut.getId());
        assertEquals(dtoIn.getStart(), dtoOut.getStart());
        assertEquals(dtoIn.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.WAITING, dtoOut.getStatus());
    }

    @Test
    void findById() {
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        BookingDtoIn dtoIn = generator.nextObject(BookingDtoIn.class);
        dtoIn.setStart(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)));
        dtoIn.setEnd(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)));
        dtoIn.setItemId(item.getId());
        BookingDtoOut dtoOutPost = bookingService.create(dtoIn, booker.getId());
        BookingDtoOut dtoOutById = bookingService.findById(dtoOutPost.getId(), owner.getId());
        assertEquals(dtoOutPost, dtoOutById);
    }

    @Test
    void patch() {
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());
        BookingDtoOut dtoOut = bookingService.patch(booking.getId(), owner.getId(), true);
        assertNotNull(dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.APPROVED, dtoOut.getStatus());
    }

    @Test
    void findAllByItemOwnerIdAndState_whenStateAll() {
        State state = State.ALL;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByItemOwnerId(from, limit, owner.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut dtoOut = bookingDtoOuts.get(0);
        assertNotNull(dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.WAITING, dtoOut.getStatus());
    }

    @Test
    void findAllByItemOwnerIdAndState_whenStateCurrent() {
        State state = State.CURRENT;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().minusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByItemOwnerId(from, limit, owner.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut dtoOut = bookingDtoOuts.get(0);
        assertNotNull(dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.WAITING, dtoOut.getStatus());
    }

    @Test
    void findAllByItemOwnerIdAndState_whenStateFuture() {
        State state = State.FUTURE;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByItemOwnerId(from, limit, owner.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut dtoOut = bookingDtoOuts.get(0);
        assertNotNull(dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.WAITING, dtoOut.getStatus());
    }

    @Test
    void findAllByItemOwnerIdAndState_whenStatePast() {
        State state = State.PAST;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().minusDays(2).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().minusDays(1).format(format)))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByItemOwnerId(from, limit, owner.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut dtoOut = bookingDtoOuts.get(0);
        assertNotNull(dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.WAITING, dtoOut.getStatus());
    }

    @Test
    void findAllByItemOwnerIdAndState_whenStateRejected() {
        State state = State.REJECTED;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .booker(booker)
                .status(Status.REJECTED)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByItemOwnerId(from, limit, owner.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut dtoOut = bookingDtoOuts.get(0);
        assertNotNull(dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.REJECTED, dtoOut.getStatus());
    }

    @Test
    void findAllByItemOwnerIdAndState_whenStateWaiting() {
        State state = State.WAITING;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByItemOwnerId(from, limit, owner.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut dtoOut = bookingDtoOuts.get(0);
        assertNotNull(dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.WAITING, dtoOut.getStatus());
    }

    @Test
    void findAllByBookerIdAndState_whenStateAll() {
        State state = State.ALL;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByBookerId(from, limit, booker.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut dtoOut = bookingDtoOuts.get(0);
        assertNotNull(dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.WAITING, dtoOut.getStatus());
    }

    @Test
    void findAllByBookerIdAndState_whenStateCurrent() {
        State state = State.CURRENT;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().minusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByBookerId(from, limit, booker.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut dtoOut = bookingDtoOuts.get(0);
        assertNotNull(dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.WAITING, dtoOut.getStatus());
    }

    @Test
    void findAllByBookerIdAndState_whenStateFuture() {
        State state = State.FUTURE;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByBookerId(from, limit, booker.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut dtoOut = bookingDtoOuts.get(0);
        assertNotNull(dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.WAITING, dtoOut.getStatus());
    }

    @Test
    void findAllByBookerIdAndState_whenStatePast() {
        State state = State.PAST;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().minusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().minusDays(1).format(format)))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByBookerId(from, limit, booker.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut dtoOut = bookingDtoOuts.get(0);
        assertNotNull(dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.WAITING, dtoOut.getStatus());
    }

    @Test
    void findAllByBookerIdAndState_whenStateRejected() {
        State state = State.REJECTED;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .booker(booker)
                .status(Status.REJECTED)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByBookerId(from, limit, booker.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut dtoOut = bookingDtoOuts.get(0);
        assertNotNull(dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.REJECTED, dtoOut.getStatus());
    }

    @Test
    void findAllByBookerIdAndState_whenStateWaiting() {
        State state = State.WAITING;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByBookerId(from, limit, booker.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut dtoOut = bookingDtoOuts.get(0);
        assertNotNull(dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booker.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.WAITING, dtoOut.getStatus());
    }
}
