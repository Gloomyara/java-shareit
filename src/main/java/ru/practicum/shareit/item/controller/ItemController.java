package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ru.practicum.shareit.util.UtilConstants.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(ITEM_PATH)
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoOut> getAllByOwnerId(
            @RequestParam(defaultValue = DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = DEFAULT_LIMIT) @Positive Integer limit,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long ownerId) {
        log.info("Received GET {} request, from = {}, limit = {}, ownerId = {}.",
                ITEM_PATH, from, limit, ownerId);
        return itemService.findAllByOwnerId(from, limit, ownerId);
    }

    @GetMapping("search")
    public List<ItemDtoOut> searchByNameOrDescription(
            @RequestParam(defaultValue = DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = DEFAULT_LIMIT) @Positive Integer limit,
            @RequestParam String text) {
        log.info("Received GET {}/search request, from = {}, limit = {}, text = {}.",
                ITEM_PATH, from, limit, text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.searchByNameOrDescription(from, limit, text);
    }

    @GetMapping("{id}")
    public ItemDtoOut getById(
            @PathVariable("id") @Positive Long itemId,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long ownerId) {
        log.info("Received GET {}/{} request, ownerId = {}.",
                ITEM_PATH, itemId, ownerId);
        return itemService.findById(itemId, ownerId);
    }

    @PostMapping
    public ItemDtoOut post(
            @Valid @RequestBody ItemDtoIn dtoIn,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long ownerId) {
        log.info("Received POST {} request, dtoIn = {}, ownerId = {}.",
                ITEM_PATH, dtoIn, ownerId);
        return itemService.create(dtoIn, ownerId);
    }

    @PutMapping
    public ItemDtoOut put(
            @Valid @RequestBody ItemDtoIn dtoIn,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long ownerId) {
        log.info("Received PUT {} request, dtoIn = {}, ownerId = {}.",
                ITEM_PATH, dtoIn, ownerId);
        return itemService.update(dtoIn, ownerId);
    }

    @PatchMapping("{id}")
    public ItemDtoOut patch(
            @PathVariable("id") @Positive Long itemId,
            @RequestBody Map<String, Object> fields,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long ownerId) {
        log.info("Received PATCH {} request, itemId = {}, fields = {}, ownerId = {}.",
                ITEM_PATH, itemId, fields, ownerId);
        return itemService.patch(itemId, fields, ownerId);
    }

    @PostMapping("{id}/comment")
    public CommentDtoOut postComment(
            @PathVariable("id") Long itemId,
            @RequestHeader(value = OWNER_ID_HEADER) @Positive Long authorId,
            @Valid @RequestBody CommentDtoIn commentDtoIn) {
        log.info("Received POST {}/{}/comment request, authorId = {}, commentDtoIn = {}.",
                ITEM_PATH, itemId, authorId, commentDtoIn);
        return itemService.createComment(itemId, authorId, commentDtoIn);
    }
}
