package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.state.State;

import java.util.List;

public interface BookingService {

    BookingDtoOut findById(Long objectId, Long userId);

    BookingDtoOut create(BookingDtoIn bookingDtoIn, Long bookerId);

    BookingDtoOut update(BookingDtoIn bookingDtoIn, Long bookerId);

    BookingDtoOut patch(Long bookingId, Long itemOwnerId, boolean approved);

    List<BookingDtoOut> findAllByBookerId(Integer from, Integer limit, Long bookerId, State state);

    List<BookingDtoOut> findAllByItemOwnerId(Integer from, Integer limit, Long ownerId, State state);

}
