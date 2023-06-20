package ru.practicum.shareit.item.repository;


import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Collection<Item> findByUserId(long userId);

    Optional<Item> getById(long itemId);

    Item patch(Item item);

    Collection<Item> itemsSearch(String text);

    Item save(Item item);

    void deleteByUserIdAndItemId(long userId, long itemId);

    void deleteByUserId(long userId);
}