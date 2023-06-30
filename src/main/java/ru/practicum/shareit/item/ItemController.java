package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

import static ru.practicum.shareit.utility.UtilConstants.OWNER_ID_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> get(@RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.debug("Получен запрос на список предметов пользователя: {}", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestParam(name = "text") String text) {
        log.debug("Получен запрос на поиск предметов по тексту: {}", text);
        return itemService.searchItems(text);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        log.debug("Получен запрос на поиск предмета по идентификатору: {}", itemId);
        return itemService.getById(itemId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(OWNER_ID_HEADER) Long userId,
                          @RequestBody @Valid ItemDto itemDto) {
        log.debug("Получен запрос на добавление данных о предмете: {}, пользователем: {}",
                itemDto.getName(), userId);
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patch(@RequestHeader(OWNER_ID_HEADER) Long userId,
                         @PathVariable Long itemId,
                         @RequestBody ItemDto itemDto) {
        log.debug("Получен запрос на обновление данных о предмете: {}, пользователем: {}",
                itemId, userId);
        return itemService.patch(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(OWNER_ID_HEADER) Long userId,
                           @PathVariable Long itemId) {
        log.debug("Получен запрос на удаление данных о предмете: {}, пользователем: {}",
                itemId, userId);
        itemService.deleteItem(userId, itemId);
    }
}