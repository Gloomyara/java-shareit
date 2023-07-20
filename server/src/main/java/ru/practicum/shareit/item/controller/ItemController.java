package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.CommentDtoIn;
import ru.practicum.shareit.item.ItemDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Map;

import static ru.practicum.shareit.util.UtilConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ITEM_PATH)
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoOut> getAllByOwnerId(
            @RequestParam(defaultValue = DEFAULT_FROM) Integer from,
            @RequestParam(value = "size", defaultValue = DEFAULT_LIMIT) Integer limit,
            @RequestHeader(value = OWNER_ID_HEADER) Long ownerId) {
        log.info("Received GET {} request, from = {}, limit = {}, ownerId = {}.",
                ITEM_PATH, from, limit, ownerId);
        return itemService.findAllByOwnerId(from, limit, ownerId);
    }

    @GetMapping("search")
    public List<ItemDtoOut> searchByNameOrDescription(
            @RequestParam(defaultValue = DEFAULT_FROM) Integer from,
            @RequestParam(value = "size", defaultValue = DEFAULT_LIMIT) Integer limit,
            @RequestParam String text) {
        log.info("Received GET {}/search request, from = {}, limit = {}, text = {}.",
                ITEM_PATH, from, limit, text);
        return itemService.searchByNameOrDescription(from, limit, text);
    }

    @GetMapping("{id}")
    public ItemDtoOut getById(
            @PathVariable("id") Long itemId,
            @RequestHeader(value = OWNER_ID_HEADER) Long ownerId) {
        log.info("Received GET {}/{} request, ownerId = {}.",
                ITEM_PATH, itemId, ownerId);
        return itemService.findById(itemId, ownerId);
    }

    @PostMapping
    public ItemDtoOut post(
            @RequestBody ItemDtoIn dtoIn,
            @RequestHeader(value = OWNER_ID_HEADER) Long ownerId) {
        log.info("Received POST {} request, dtoIn = {}, ownerId = {}.",
                ITEM_PATH, dtoIn, ownerId);
        return itemService.save(dtoIn, ownerId);
    }

    @PutMapping
    public ItemDtoOut put(
            @RequestBody ItemDtoIn dtoIn,
            @RequestHeader(value = OWNER_ID_HEADER) Long ownerId) {
        log.info("Received PUT {} request, dtoIn = {}, ownerId = {}.",
                ITEM_PATH, dtoIn, ownerId);
        return itemService.update(dtoIn, ownerId);
    }

    @PatchMapping("{id}")
    public ItemDtoOut patch(
            @PathVariable("id") Long itemId,
            @RequestBody Map<String, Object> fields,
            @RequestHeader(value = OWNER_ID_HEADER) Long ownerId) {
        log.info("Received PATCH {} request, itemId = {}, fields = {}, ownerId = {}.",
                ITEM_PATH, itemId, fields, ownerId);
        return itemService.patch(itemId, fields, ownerId);
    }

    @PostMapping("{id}/comment")
    public CommentDtoOut postComment(
            @PathVariable("id") Long itemId,
            @RequestHeader(value = OWNER_ID_HEADER) Long authorId,
            @RequestBody CommentDtoIn commentDtoIn) {
        log.info("Received POST {}/{}/comment request, authorId = {}, commentDtoIn = {}.",
                ITEM_PATH, itemId, authorId, commentDtoIn);
        return itemService.createComment(itemId, authorId, commentDtoIn);
    }
}
