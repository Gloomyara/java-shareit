package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestDtoIn;
import ru.practicum.shareit.request.client.RequestClient;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.Objects;

import static ru.practicum.shareit.util.UtilConstants.OWNER_ID_HEADER;
import static ru.practicum.shareit.util.UtilConstants.REQUESTS_PATH;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(REQUESTS_PATH)
public class RequestGatewayController {

    private final RequestClient client;

    @GetMapping("{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") @Positive Long id,
                                          @RequestHeader(value = OWNER_ID_HEADER) @Positive Long authorId) {
        log.info("Received GET {}/{} request, authorId = {}.",
                REQUESTS_PATH, id, authorId);
        return client.getById(id, authorId);
    }

    @PostMapping
    public ResponseEntity<Object> post(@RequestBody @Valid RequestDtoIn dtoIn,
                                       @RequestHeader(value = OWNER_ID_HEADER) @Positive Long authorId) {
        log.info("Received POST {} request, dtoIn = {}, authorId = {}.",
                REQUESTS_PATH, dtoIn, authorId);
        return client.post(dtoIn, authorId);
    }

    @GetMapping("all")
    public ResponseEntity<Object> findAll(
            @RequestParam(required = false) @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false) @Positive Integer limit,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long authorId) {
        log.info("Received GET {}/all request, from = {}, limit = {}, authorId = {}.",
                REQUESTS_PATH, from, limit, authorId);
        if (Objects.isNull(from) || Objects.isNull(limit)) {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        }
        return client.findAll(from, limit, authorId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllCreatedByUser(
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long authorId) {
        log.info("Received GET {}/all request, authorId = {}.",
                REQUESTS_PATH, authorId);
        return client.findAllCreatedByUser(authorId);
    }
}
