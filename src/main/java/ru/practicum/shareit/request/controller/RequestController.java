package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.util.UtilConstants.OWNER_ID_HEADER;
import static ru.practicum.shareit.util.UtilConstants.REQUESTS_PATH;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(REQUESTS_PATH)
public class RequestController {

    private final RequestService requestService;

    @GetMapping("{id}")
    public RequestDtoOut get(@PathVariable("id") @Positive Long id,
                             @RequestHeader(value = OWNER_ID_HEADER) @Positive Long authorId) {
        log.info("Received GET {}/{} request, authorId = {}.",
                REQUESTS_PATH, id, authorId);
        return requestService.findById(id, authorId);
    }

    @PostMapping
    public RequestDtoOut post(@Valid @RequestBody RequestDtoIn dtoIn,
                              @RequestHeader(value = OWNER_ID_HEADER) @Positive Long authorId) {
        log.info("Received POST {} request, dtoIn = {}, authorId = {}.",
                REQUESTS_PATH, dtoIn, authorId);
        return requestService.create(dtoIn, authorId);
    }

    @GetMapping("all")
    public List<RequestDtoOut> findAll(
            @RequestParam(required = false) @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false) @Positive Integer limit,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long authorId) {
        log.info("Received GET {}/all request, from = {}, limit = {}, authorId = {}.",
                REQUESTS_PATH, from, limit, authorId);
        if (Objects.isNull(from) || Objects.isNull(limit)) {
            return Collections.emptyList();
        }
        return requestService.findAll(from, limit, authorId);
    }

    @GetMapping
    public List<RequestDtoOut> findAllCreatedByUser(
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long authorId) {
        log.info("Received GET {}/all request, authorId = {}.",
                REQUESTS_PATH, authorId);
        return requestService.findAllByAuthorId(authorId);
    }
}
