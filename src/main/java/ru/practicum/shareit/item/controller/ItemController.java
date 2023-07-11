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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ru.practicum.shareit.util.UtilConstants.OWNER_ID_HEADER;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService service;

    @GetMapping
    List<ItemDtoOut> findAllByUserId(@RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId) {
        log.info("Получен запрос на список предметов пользователя: {}", userId);
        return service.findAllByOwnerId(userId);
    }

    @GetMapping("search")
    List<ItemDtoOut> searchByText(@RequestParam String text) {
        log.info("Получен запрос на поиск предметов по тексту: {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return service.searchByText(text);
    }

    @GetMapping("{id}")
    ItemDtoOut findById(@PathVariable("id") @Positive Long itemId,
                        @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId) {
        log.info("Получен запрос на поиск предмета по идентификатору: {}", itemId);
        return service.findById(itemId, userId);
    }

    @PostMapping
    ItemDtoOut post(@Valid @RequestBody ItemDtoIn dtoIn,
                    @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId) {
        log.info("Получен запрос на добавление данных о предмете: {}, пользователем: {}",
                dtoIn.getName(), userId);
        return service.create(dtoIn, userId);
    }

    @PutMapping
    ItemDtoOut put(@Valid @RequestBody ItemDtoIn dtoIn,
                   @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId) {
        log.info("Получен запрос на обновление данных о предмете: {}, пользователем: {}",
                dtoIn, userId);
        return service.update(dtoIn, userId);
    }

    @PatchMapping("{id}")
    ItemDtoOut patch(@PathVariable("id") @Positive Long itemId,
                     @RequestBody Map<String, Object> fields,
                     @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId) {
        log.info("Получен запрос на обновление данных о предмете: {}, пользователем: {}",
                itemId, userId);
        return service.patch(itemId, fields, userId);
    }

    @PostMapping("{id}/comment")
    CommentDtoOut postComment(@PathVariable("id") Long itemId,
                              @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId,
                              @Valid @RequestBody CommentDtoIn dtoIn) {
        log.info("Получен запрос на добавление комментария для предмета: {}, пользователем: {}",
                itemId, userId);
        return service.createComment(itemId, userId, dtoIn);
    }
}
