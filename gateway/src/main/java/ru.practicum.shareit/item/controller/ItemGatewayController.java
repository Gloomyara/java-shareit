package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.CommentDtoIn;
import ru.practicum.shareit.item.ItemDtoIn;
import ru.practicum.shareit.item.client.ItemClient;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.Map;

import static ru.practicum.shareit.util.UtilConstants.*;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(ITEM_PATH)
public class ItemGatewayController {

    private final ItemClient client;

    @GetMapping
    public ResponseEntity<Object> getAllByOwnerId(
            @RequestParam(defaultValue = DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = DEFAULT_LIMIT) @Positive Integer limit,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long ownerId) {
        log.info("Received GET {} request, from = {}, limit = {}, ownerId = {}.",
                ITEM_PATH, from, limit, ownerId);
        return client.getAllByOwnerId(from, limit, ownerId);
    }

    @GetMapping("search")
    public ResponseEntity<Object> searchByNameOrDescription(
            @RequestParam(defaultValue = DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = DEFAULT_LIMIT) @Positive Integer limit,
            @RequestParam String text,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId) {
        log.info("Received GET {}/search request, from = {}, limit = {}, text = {}.",
                ITEM_PATH, from, limit, text);
        if (text.isBlank()) {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        }
        return client.searchByNameOrDescription(from, limit, text, userId);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getById(
            @PathVariable("id") @Positive Long itemId,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long ownerId) {
        log.info("Received GET {}/{} request, ownerId = {}.",
                ITEM_PATH, itemId, ownerId);
        return client.getById(itemId, ownerId);
    }

    @PostMapping
    public ResponseEntity<Object> post(
            @RequestBody @Valid ItemDtoIn dtoIn,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long ownerId) {
        log.info("Received POST {} request, dtoIn = {}, ownerId = {}.",
                ITEM_PATH, dtoIn, ownerId);
        return client.post(dtoIn, ownerId);
    }

    @PutMapping
    public ResponseEntity<Object> put(
            @RequestBody @Valid ItemDtoIn dtoIn,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long ownerId) {
        log.info("Received PUT {} request, dtoIn = {}, ownerId = {}.",
                ITEM_PATH, dtoIn, ownerId);
        return client.put(dtoIn, ownerId);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> patch(
            @PathVariable("id") @Positive Long itemId,
            @RequestBody Map<String, Object> fields,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long ownerId) {
        log.info("Received PATCH {} request, itemId = {}, fields = {}, ownerId = {}.",
                ITEM_PATH, itemId, fields, ownerId);
        return client.patch(itemId, fields, ownerId);
    }

    @PostMapping("{id}/comment")
    public ResponseEntity<Object> postComment(
            @PathVariable("id") Long itemId,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long authorId,
            @RequestBody @Valid CommentDtoIn commentDtoIn) {
        log.info("Received POST {}/{}/comment request, authorId = {}, commentDtoIn = {}.",
                ITEM_PATH, itemId, authorId, commentDtoIn);
        return client.postComment(itemId, authorId, commentDtoIn);
    }
}
