package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.abstraction.model.DtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.state.State;

import java.util.List;
import java.util.Map;

public interface BookingService {

    BookingDtoOut findById(Long bookingId, Long userId);

    BookingDtoOut findById(Long objectId);

    List<BookingDtoOut> findAllByUserId(Long userId);

    BookingDtoOut patch(Long bookingId, Long userId, Boolean approved);

    BookingDtoOut patch(Long id, Map<String, Object> fields, Long userId);

    BookingDtoOut create(DtoIn in, Long userId);

    BookingDtoOut update(DtoIn in, Long userId);

    List<BookingDtoOut> findAllByBookerId(Long userId, State state);

    List<BookingDtoOut> findAllByOwnerId(Long userId, State state);

}
