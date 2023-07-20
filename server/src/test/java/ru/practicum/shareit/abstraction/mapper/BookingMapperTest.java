package ru.practicum.shareit.abstraction.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest extends AbstractModelMapperTest<BookingDtoIn, BookingDtoOut, Booking, BookingMapper> {

    private final BookingShort bookingShort = new SpelAwareProxyProjectionFactory().createProjection(BookingShort.class);

    private final Long userId = generator.nextLong();
    private final String userName = generator.nextObject(String.class);
    private final String userEmail = generator.nextObject(String.class);

    private final Long bookingId = generator.nextLong();
    private final LocalDateTime bookingStart = generator.nextObject(LocalDateTime.class);
    private final LocalDateTime bookingEnd = generator.nextObject(LocalDateTime.class);
    private final Status bookingStatus = generator.nextObject(Status.class);

    private final User user = User.builder()
            .id(userId)
            .name(userName)
            .email(userEmail)
            .build();

    private final UserDtoShort userDtoShort = UserDtoShort.builder()
            .id(userId)
            .build();

    private final Booking booking = Booking.builder()
            .id(bookingId)
            .start(bookingStart)
            .end(bookingEnd)
            .booker(user)
            .status(bookingStatus)
            .build();

    private final BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
            .id(bookingId)
            .start(bookingStart)
            .end(bookingEnd)
            .build();

    private final BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
            .id(bookingId)
            .start(bookingStart)
            .end(bookingEnd)
            .booker(userDtoShort)
            .status(bookingStatus)
            .build();


    private final BookingDtoShort bookingDtoShort = BookingDtoShort.builder()
            .id(bookingId)
            .bookerId(userId)
            .build();

    protected BookingMapperTest() {
        super(new BookingMapper());
    }

    @Override
    protected Booking getEntity() {
        return booking;
    }

    @Override
    protected BookingDtoIn getDtoIn() {
        return bookingDtoIn;
    }

    @Override
    protected BookingDtoOut getDtoOut() {
        return bookingDtoOut;
    }

    @Test
    @Override
    void toEntityTest() {
        Booking b = mapper.toEntity(getDtoIn());
        b.setStatus(bookingStatus);
        assertEquals(getEntity(), b);
    }

    @Test
    void toDtoShortTest() {
        bookingShort.setId(bookingId);
        bookingShort.setBookerId(userId);
        BookingDtoShort b = mapper.toDtoShort(bookingShort);
        assertEquals(bookingDtoShort, b);
    }
}
