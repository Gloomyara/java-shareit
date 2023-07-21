package ru.practicum.shareit.abstraction.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Map;

class AbstractBookingServiceTest extends AbstractServiceTest<Booking> {

    private final Long bookingId = generator.nextLong();
    private final LocalDateTime bookingStart = generator.nextObject(LocalDateTime.class);
    private final LocalDateTime bookingEnd = generator.nextObject(LocalDateTime.class);
    private final Status bookingStatus = generator.nextObject(Status.class);

    private final LocalDateTime updatedBookingStart = generator.nextObject(LocalDateTime.class);
    private final LocalDateTime updatedBookingEnd = generator.nextObject(LocalDateTime.class);

    @Override
    protected Booking getEntity() {
        return Booking.builder()
                .id(bookingId)
                .start(bookingStart)
                .end(bookingEnd)
                .status(bookingStatus)
                .build();
    }

    @Override
    protected Booking getUpdated() {
        return Booking.builder()
                .id(bookingId)
                .start(updatedBookingStart)
                .end(updatedBookingEnd)
                .status(bookingStatus)
                .build();
    }

    @Override
    protected Map<String, Object> getFields() {
        return Map.of(
                "start", updatedBookingStart.toString(),
                "end", updatedBookingEnd.toString()
        );
    }
}
