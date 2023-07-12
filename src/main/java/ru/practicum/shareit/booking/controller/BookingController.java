package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.state.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.shareit.util.UtilConstants.OWNER_ID_HEADER;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    List<BookingDtoOut> getAllByBookerId(
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Получен запрос на получение списка предметов, " +
                "которые брались в аренду пользователем ид: {}", userId);
        return bookingService.findAllByBookerId(userId, State.fromString(state));
    }

    @GetMapping("owner")
    List<BookingDtoOut> getAllByOwnerId(
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Получен запрос на получение списка предметов, " +
                "которые сдавались в аренду владельцем ид: {}", userId);
        return bookingService.findAllByOwnerId(userId, State.fromString(state));
    }

    @GetMapping("{id}")
    BookingDtoOut getById(@PathVariable("id") @Positive Long bookingId,
                          @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId) {
        log.info("Получен запрос на поиск аренды по идентификатору: {}", bookingId);
        return bookingService.findById(bookingId, userId);
    }

    @PostMapping
    BookingDtoOut post(@Valid @RequestBody BookingDtoIn in,
                       @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId) {
        log.info("Получен запрос на создание аренды: {}", in);
        return bookingService.create(in, userId);
    }

    @PutMapping
    BookingDtoOut put(@Valid @RequestBody BookingDtoIn in,
                      @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId) {
        log.info("Получен запрос на обновление аренды: {}", in);
        return bookingService.update(in, userId);
    }

    @PatchMapping("{id}")
    BookingDtoOut patch(@PathVariable("id") @Positive Long bookingId,
                        @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId,
                        @RequestParam Boolean approved) {
        log.info("Получен запрос на обновление статуса аренды: {}", approved);
        return bookingService.patch(bookingId, userId, approved);
    }
}
