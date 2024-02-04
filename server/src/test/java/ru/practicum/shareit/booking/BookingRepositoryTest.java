package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;

    private final LocalDateTime start = LocalDateTime.of(2098, 1, 1, 1, 1, 1);
    private final LocalDateTime end = LocalDateTime.of(2099, 1, 1, 1, 1, 1);

    private final User user = User.builder()
            .name("test1")
            .email("test1@test.omg")
            .build();

    private final Item item = Item.builder()
            .name("test_item")
            .description("test_item_desc")
            .available(true)
            .owner(user)
            .build();

    private final Booking futureBooking = Booking.builder()
            .start(start)
            .end(end)
            .item(item)
            .booker(user)
            .status(Status.WAITING)
            .build();

    private final Booking futureBookingIsApproved = Booking.builder()
            .start(start)
            .end(end)
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .build();

    private final Booking currentBooking = Booking.builder()
            .start(LocalDateTime.now().minusDays(1))
            .end(LocalDateTime.now().plusDays(1))
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .build();

    private final Booking oldBooking = Booking.builder()
            .start(start.minusYears(1000))
            .end(end.minusYears(1000))
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .build();

    @BeforeEach
    void setUp() {
        entityManager.persist(user);
        entityManager.persist(item);
    }

    @Test
    void findByIdWithUserAndItem() {
        entityManager.persist(currentBooking);
        Optional<Booking> foundBookings = bookingRepository.findByIdWithBookerAndItem(currentBooking.getId());
        assertThat(foundBookings).isPresent();
    }

    @Test
    void existsBookingByItemIdAndBookerId_assertFalse() {
        entityManager.persist(futureBooking);
        boolean b = bookingRepository
                .existsBookingByItemIdAndBookerId(item.getId(), user.getId());
        assertFalse(b);
    }

    @Test
    void existsBookingByItemIdAndBookerId_assertTrue() {
        entityManager.persist(oldBooking);
        boolean b = bookingRepository
                .existsBookingByItemIdAndBookerId(item.getId(), user.getId());
        assertTrue(b);
    }

    @Test
    void findAllByBookerId() {
        entityManager.persist(futureBooking);
        Page<Booking> foundBookings = bookingRepository
                .findAllByBookerId(user.getId(), Pageable.ofSize(10));
        assertThat(foundBookings.toList()).hasSize(1);
    }

    @Test
    void findAllByItemOwnerId() {
        entityManager.persist(futureBooking);
        Page<Booking> foundBookings = bookingRepository
                .findAllByItemOwnerId(user.getId(), Pageable.ofSize(10));
        assertThat(foundBookings.toList()).hasSize(1);
    }

    @Test
    void findAllByBookerIdWhereStartIsAfterCurrent() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        Page<Booking> foundBookings = bookingRepository
                .findAllByBookerIdWhereStartInFuture(user.getId(), Pageable.ofSize(10));
        assertThat(foundBookings.toList()).hasSize(1);
    }

    @Test
    void findAllByItemOwnerIdWhereStartIsAfterCurrent() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        Page<Booking> foundBookings = bookingRepository
                .findAllByItemOwnerIdWhereStartInFuture(user.getId(), Pageable.ofSize(10));
        assertThat(foundBookings.toList()).hasSize(1);
    }

    @Test
    void findAllByBookerIdWhereEndBeforeCurrent() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        Page<Booking> foundBookings = bookingRepository
                .findAllByBookerIdWhereEndInPast(user.getId(), Pageable.ofSize(10));
        assertThat(foundBookings.toList()).hasSize(1);
    }

    @Test
    void findAllByItemOwnerIdWhereEndBeforeCurrent() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        Page<Booking> foundBookings = bookingRepository
                .findAllByItemOwnerIdWhereEndInPast(user.getId(), Pageable.ofSize(10));
        assertThat(foundBookings.toList()).hasSize(1);
    }

    @Test
    void findAllByBookerIdWhereCurrentBetweenStartAndEnd() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        Page<Booking> foundBookings = bookingRepository
                .findAllByBookerIdWithTimestampsBetweenStartAndEnd(user.getId(), Pageable.ofSize(10));
        assertThat(foundBookings.toList()).hasSize(1);
    }

    @Test
    void findBookingsByItemOwnerIdAndStatus() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        Page<Booking> foundBookings = bookingRepository
                .findBookingsByItemOwnerIdAndStatus(user.getId(), Status.WAITING, Pageable.ofSize(10));
        assertThat(foundBookings.toList()).hasSize(1);
        assertThat(foundBookings.toList().get(0)).isEqualTo(futureBooking);
    }

    @Test
    void findLastBookingsByItemOwnerId() {
        entityManager.persist(futureBookingIsApproved);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        List<BookingShort> foundBookings = bookingRepository
                .findLastBookingsByItemOwnerId(user.getId());
        assertThat(foundBookings).hasSize(1);
    }

    @Test
    void findNextBookingsByItemOwnerId() {
        entityManager.persist(futureBookingIsApproved);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        List<BookingShort> foundBookings = bookingRepository
                .findNextBookingsByItemOwnerId(user.getId());
        assertThat(foundBookings).hasSize(1);
    }

    @Test
    void existsByBookingIdAndBookerOrItemOwnerId_assertTrue() {
        entityManager.persist(currentBooking);
        assertTrue(bookingRepository.existsByBookingIdAndBookerOrItemOwnerId(currentBooking.getId(), user.getId()));
    }

    @Test
    void existsByBookingIdAndBookerOrItemOwnerId_assertFalse() {
        entityManager.persist(currentBooking);
        assertFalse(bookingRepository.existsByBookingIdAndBookerOrItemOwnerId(currentBooking.getId(), 999L));
    }

    @Test
    void findBookingsByBookerIdAndStatus() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        Page<Booking> foundBookings = bookingRepository
                .findBookingsByBookerIdAndStatus(user.getId(), Status.WAITING, Pageable.ofSize(10));
        assertThat(foundBookings.toList()).hasSize(1);
        assertThat(foundBookings.toList().get(0)).isEqualTo(futureBooking);
    }

    @Test
    void findLastBookingByItemId() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        Optional<BookingShort> foundBooking = bookingRepository
                .findTopByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                        item.getId(), Status.APPROVED, LocalDateTime.now());
        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get()).hasFieldOrPropertyWithValue("id", currentBooking.getId());
    }

    @Test
    void findNextBookingByItemId() {
        entityManager.persist(futureBookingIsApproved);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        Optional<BookingShort> foundBooking = bookingRepository
                .findTopByItemIdAndStatusAndStartAfterOrderByStart(
                        item.getId(), Status.APPROVED, LocalDateTime.now());
        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get()).hasFieldOrPropertyWithValue("id", futureBookingIsApproved.getId());
    }
}
