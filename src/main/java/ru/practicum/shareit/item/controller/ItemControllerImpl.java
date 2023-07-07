package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemControllerImpl implements ItemController {

    private final ItemService service;

    @Override
    public List<ItemDtoOut> findAllByUserId(Long userId) {
        log.debug("Получен запрос на список предметов пользователя: {}", userId);
        return service.findAllByUserId(userId);
    }

    @Override
    public List<ItemDtoOut> searchByText(String text) {
        log.debug("Получен запрос на поиск предметов по тексту: {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return service.searchByText(text);
    }

    @Override
    public ItemDtoOut findById(Long itemId, Long userId) {
        log.debug("Получен запрос на поиск предмета по идентификатору: {}", itemId);
        return service.findById(itemId, userId);
    }

    @Override
    public ItemDtoOut post(ItemDtoIn itemDtoIn, Long userId) {
        log.debug("Получен запрос на добавление данных о предмете: {}, пользователем: {}",
                itemDtoIn.getName(), userId);
        return service.create(itemDtoIn, userId);
    }

    @Override
    public ItemDtoOut put(ItemDtoIn itemDtoIn, Long userId) {
        log.debug("Получен запрос на обновление данных о предмете: {}, пользователем: {}",
                itemDtoIn, userId);
        return service.update(itemDtoIn, userId);
    }

    @Override
    public ItemDtoOut patch(Long itemId, Map<String, Object> fields, Long userId) {
        log.debug("Получен запрос на обновление данных о предмете: {}, пользователем: {}",
                itemId, userId);
        return service.patch(itemId, fields, userId);
    }

    @Override
    public CommentDtoOut postComment(Long itemId, Long userId, CommentDtoIn commentDtoIn) {
        log.debug("Получен запрос на добавление комментария для предмета: {}, пользователем: {}",
                itemId, userId);
        return service.createComment(itemId, userId, commentDtoIn);
    }
}
