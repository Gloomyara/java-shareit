package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto patch(long userId, long itemId, ItemDto itemDto);

    ItemDto getById(long itemId);

    Collection<ItemDto> searchItems(String text);

    ItemDto addNewItem(long userId, ItemDto itemDto);

    Collection<ItemDto> getItems(long userId);

    void deleteItem(long userId, long itemId);
}
