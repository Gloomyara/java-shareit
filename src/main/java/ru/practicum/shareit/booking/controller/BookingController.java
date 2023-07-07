package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.state.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.shareit.util.UtilConstants.OWNER_ID_HEADER;

public interface BookingController {

    @GetMapping
    List<BookingDtoOut> getAllByBookerId(@RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId,
                                         @RequestParam(value = "state",
                                               required = false,
                                               defaultValue = "ALL") State state);

    @GetMapping("owner")
    List<BookingDtoOut> getAllByOwnerId(@RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId,
                                        @RequestParam(value = "state",
                                                required = false,
                                                defaultValue = "ALL") State state);

    @GetMapping("{id}")
    BookingDtoOut getById(@PathVariable("id") @Positive Long bookingId,
                          @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId);

    @PostMapping
    BookingDtoOut post(@Valid @RequestBody BookingDtoIn in,
                       @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId);

    @PutMapping
    BookingDtoOut put(@Valid @RequestBody BookingDtoIn in,
                      @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId);

    @PatchMapping("{id}")
    BookingDtoOut patch(@PathVariable("id") @Positive Long bookingId,
                        @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId,
                        @RequestParam Boolean approved);

}
