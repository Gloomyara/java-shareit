package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Map<Long, Item>> items = new HashMap<>();
    private final AtomicLong lastId = new AtomicLong(1);

    @Override
    public Collection<Item> findByUserId(long userId) {
        return items.getOrDefault(userId, Collections.emptyMap()).values();
    }

    @Override
    public Item save(Item item) {
        item.setId(lastId.getAndIncrement());
        items.compute(item.getOwner().getId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new HashMap<>();
            }
            userItems.put(item.getId(), item);
            return userItems;
        });
        return item;
    }

    @Override
    public Optional<Item> getById(long itemId) {
        return items.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst();
    }

    @Override
    public Item patch(Item item) {
        long userId = item.getOwner().getId();
        long itemId = item.getId();
        if (items.containsKey(userId) &&
                items.get(userId).containsKey(itemId)) {
            Item oldItem = items.get(userId).get(itemId);
            if (item.getName() != null && !item.getName().isBlank()) {
                oldItem.setName(item.getName());
            }
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                oldItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                oldItem.setAvailable(item.getAvailable());
            }
            return oldItem;
        }
        log.warn("Item not found {}", item);
        throw new EntityNotFoundException("Item not found " + item);
    }

    @Override
    public Collection<Item> itemsSearch(String text) {
        return items.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        if (items.containsKey(userId)) {
            items.get(userId).remove(itemId);
        }
    }

    @Override
    public void deleteByUserId(long userId) {
        items.remove(userId);
    }
}
