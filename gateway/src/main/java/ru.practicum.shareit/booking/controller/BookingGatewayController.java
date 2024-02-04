package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.exception.RentTimeConstraintException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.UtilConstants.*;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(BOOKING_PATH)
public class BookingGatewayController {

    private final BookingClient client;

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(
            @RequestParam(defaultValue = DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = DEFAULT_LIMIT) @Positive Integer limit,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long bookerId,
            @RequestParam(value = BOOKING_STATE, defaultValue = BOOKING_STATE_DEFAULT) String str) {

        State state = State.fromString(str);
        log.info("Received GET {} request, from = {}, size = {}, bookerId = {}, state = {}.",
                BOOKING_PATH, from, limit, bookerId, state);
        return client.getAllByUserId(from, limit, bookerId, state);
    }

    @GetMapping("owner")
    public ResponseEntity<Object> getAllByOwnerId(
            @RequestParam(defaultValue = DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = DEFAULT_LIMIT) @Positive Integer limit,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long itemOwnerId,
            @RequestParam(value = BOOKING_STATE, defaultValue = BOOKING_STATE_DEFAULT) String str) {

        State state = State.fromString(str);
        log.info("Received GET {}/owner request, from = {}, size = {}, itemOwnerId = {}, state = {}.",
                BOOKING_PATH, from, limit, itemOwnerId, state);
        return client.getAllByOwnerId(from, limit, itemOwnerId, state);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getById(
            @PathVariable("id") @Positive Long bookingId,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId) {
        log.info("Received GET {}/{} request, userId = {}.",
                BOOKING_PATH, bookingId, userId);
        return client.getById(bookingId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> post(
            @RequestBody @Valid BookingDtoIn dtoIn,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long bookerId) {
        validateRentTime(dtoIn);
        log.info("Received POST {} request, bookingDtoIn = {}, bookerId = {}.",
                BOOKING_PATH, dtoIn, bookerId);
        return client.post(dtoIn, bookerId);
    }

    @PutMapping
    public ResponseEntity<Object> put(
            @RequestBody @Valid BookingDtoIn dtoIn,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long bookerId) {
        validateRentTime(dtoIn);
        log.info("Received PUT {} request, bookingDtoIn = {}, bookerId = {}.",
                BOOKING_PATH, dtoIn, bookerId);
        return client.put(dtoIn, bookerId);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> patch(
            @PathVariable("id") @Positive Long bookingId,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long itemOwnerId,
            @RequestParam boolean approved) {
        log.info("Received PATCH {}/{} request, itemOwnerId = {}, approved = {}.",
                BOOKING_PATH, bookingId, itemOwnerId, approved);
        return client.patch(bookingId, itemOwnerId, approved);
    }

    private void validateRentTime(BookingDtoIn dtoIn) {
        boolean b1 = dtoIn.getStart().isAfter(dtoIn.getEnd());
        boolean b2 = dtoIn.getStart().equals(dtoIn.getEnd());
        if (b1 || b2) {
            throw new RentTimeConstraintException();
        }
    }
}
