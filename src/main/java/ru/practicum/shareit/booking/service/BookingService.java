package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.abstraction.userreference.service.UserReferenceService;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.state.State;

import java.util.List;

public interface BookingService extends UserReferenceService<BookingDtoIn, BookingDtoOut> {

    BookingDtoOut findById(Long bookingId, Long userId);

    BookingDtoOut patch(Long bookingId, Long userId, Boolean approved);

    List<BookingDtoOut> findAllByBookerId(Long userId, State state);

    List<BookingDtoOut> findAllByOwnerId(Long userId, State state);

}
