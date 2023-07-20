package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.util.UtilConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(BOOKING_PATH)
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingDtoOut> getAllByUserId(
            @RequestParam(defaultValue = DEFAULT_FROM) Integer from,
            @RequestParam(value = "size", defaultValue = DEFAULT_LIMIT) Integer limit,
            @RequestHeader(value = OWNER_ID_HEADER) Long bookerId,
            @RequestParam(value = BOOKING_STATE, defaultValue = BOOKING_STATE_DEFAULT) String state) {
        log.info("Received GET {} request, from = {}, size = {}, bookerId = {}, state = {}.",
                BOOKING_PATH, from, limit, bookerId, state);
        return bookingService.findAllByBookerId(from, limit, bookerId, State.fromString(state));
    }

    @GetMapping("owner")
    public List<BookingDtoOut> getAllByOwnerId(
            @RequestParam(defaultValue = DEFAULT_FROM) Integer from,
            @RequestParam(value = "size", defaultValue = DEFAULT_LIMIT) Integer limit,
            @RequestHeader(value = OWNER_ID_HEADER) Long itemOwnerId,
            @RequestParam(value = BOOKING_STATE, defaultValue = BOOKING_STATE_DEFAULT) String state) {
        log.info("Received GET {}/owner request, from = {}, size = {}, itemOwnerId = {}, state = {}.",
                BOOKING_PATH, from, limit, itemOwnerId, state);
        return bookingService.findAllByItemOwnerId(from, limit, itemOwnerId, State.fromString(state));
    }

    @GetMapping("{id}")
    public BookingDtoOut getById(
            @PathVariable("id") Long bookingId,
            @RequestHeader(value = OWNER_ID_HEADER) Long userId) {
        log.info("Received GET {}/{} request, userId = {}.",
                BOOKING_PATH, bookingId, userId);
        return bookingService.findById(bookingId, userId);
    }

    @PostMapping
    public BookingDtoOut post(
            @RequestBody BookingDtoIn dtoIn,
            @RequestHeader(value = OWNER_ID_HEADER) Long bookerId) {
        log.info("Received POST {} request, bookingDtoIn = {}, bookerId = {}.",
                BOOKING_PATH, dtoIn, bookerId);
        return bookingService.save(dtoIn, bookerId);
    }

    @PutMapping
    public BookingDtoOut put(
            @RequestBody BookingDtoIn dtoIn,
            @RequestHeader(value = OWNER_ID_HEADER) Long bookerId) {
        log.info("Received PUT {} request, bookingDtoIn = {}, bookerId = {}.",
                BOOKING_PATH, dtoIn, bookerId);
        return bookingService.update(dtoIn, bookerId);
    }

    @PatchMapping("{id}")
    public BookingDtoOut patch(
            @PathVariable("id") Long bookingId,
            @RequestHeader(value = OWNER_ID_HEADER) Long itemOwnerId,
            @RequestParam boolean approved) {
        log.info("Received PATCH {}/{} request, itemOwnerId = {}, approved = {}.",
                BOOKING_PATH, bookingId, itemOwnerId, approved);
        return bookingService.patch(bookingId, itemOwnerId, approved);
    }
}
