package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.state.State;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingControllerImpl implements BookingController {

    private final BookingService bookingService;

    @Override
    public List<BookingDtoOut> getAllByBookerId(Long userId, State state) {
        log.info("Получен запрос на получение списка предметов, " +
                "которые брались в аренду пользователем ид: {}", userId);
        return bookingService.findAllByBookerId(userId, state);
    }

    @Override
    public List<BookingDtoOut> getAllByOwnerId(Long userId, State state) {
        log.info("Получен запрос на получение списка предметов, " +
                "которые сдавались в аренду владельцем ид: {}", userId);
        return bookingService.findAllByOwnerId(userId, state);
    }

    @Override
    public BookingDtoOut getById(Long objectId, Long userId) {
        log.info("Получен запрос на поиск аренды по идентификатору: {}", objectId);
        return bookingService.findById(objectId, userId);
    }

    @Override
    public BookingDtoOut post(BookingDtoIn bookingDtoIn, Long userId) {
        log.info("Получен запрос на создание аренды: {}", bookingDtoIn);
        return bookingService.create(bookingDtoIn, userId);
    }

    @Override
    public BookingDtoOut put(BookingDtoIn bookingDtoIn, Long userId) {
        log.info("Получен запрос на обновление аренды: {}", bookingDtoIn);
        return bookingService.update(bookingDtoIn, userId);
    }

    @Override
    public BookingDtoOut patch(Long id, Long userId, Boolean approved) {
        log.info("Получен запрос на обновление статуса аренды: {}", approved);
        return bookingService.patch(id, userId, approved);
    }
}
