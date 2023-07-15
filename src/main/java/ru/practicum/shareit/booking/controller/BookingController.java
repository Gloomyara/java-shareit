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
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.util.UtilConstants.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(BOOKING_PATH)
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    private static final String STATE = "state";
    private static final String STATE_DEFAULT = "ALL";

    @GetMapping
    public List<BookingDtoOut> getAllByUserId(
            @RequestParam(defaultValue = DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = DEFAULT_LIMIT) @Positive Integer limit,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long bookerId,
            @RequestParam(value = STATE, defaultValue = STATE_DEFAULT) String state) {
        log.info("Received GET {} request, from = {}, size = {}, bookerId = {}, state = {}.",
                BOOKING_PATH, from, limit, bookerId, state);
        return bookingService.findAllByBookerId(from, limit, bookerId, State.fromString(state));
    }

    @GetMapping("owner")
    public List<BookingDtoOut> getAllByOwnerId(
            @RequestParam(defaultValue = DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = DEFAULT_LIMIT) @Positive Integer limit,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long itemOwnerId,
            @RequestParam(value = STATE, defaultValue = STATE_DEFAULT) String state) {
        log.info("Received GET {}/owner request, from = {}, size = {}, itemOwnerId = {}, state = {}.",
                BOOKING_PATH, from, limit, itemOwnerId, state);
        return bookingService.findAllByItemOwnerId(from, limit, itemOwnerId, State.fromString(state));
    }

    @GetMapping("{id}")
    public BookingDtoOut getById(
            @PathVariable("id") @Positive Long bookingId,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId) {
        log.info("Received GET {}/{} request, userId = {}.",
                BOOKING_PATH, bookingId, userId);
        return bookingService.findById(bookingId, userId);
    }

    @PostMapping
    public BookingDtoOut post(
            @Valid @RequestBody BookingDtoIn dtoIn,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long bookerId) {
        log.info("Received POST {} request, bookingDtoIn = {}, bookerId = {}.",
                BOOKING_PATH, dtoIn, bookerId);
        return bookingService.create(dtoIn, bookerId);
    }

    @PutMapping
    public BookingDtoOut put(
            @Valid @RequestBody BookingDtoIn dtoIn,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long bookerId) {
        log.info("Received PUT {} request, bookingDtoIn = {}, bookerId = {}.",
                BOOKING_PATH, dtoIn, bookerId);
        return bookingService.update(dtoIn, bookerId);
    }

    @PatchMapping("{id}")
    public BookingDtoOut patch(
            @PathVariable("id") @Positive Long bookingId,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long bookerId,
            @RequestParam boolean approved) {
        log.info("Received PATCH {}/{} request, bookerId = {}, approved = {}.",
                BOOKING_PATH, bookingId, bookerId, approved);
        return bookingService.patch(bookingId, bookerId, approved);
    }
}
