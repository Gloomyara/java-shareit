package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Map<Long, Item>> items = new HashMap<>();
    private final Map<Long, Long> owners = new HashMap<>();
    private Long lastId = 1L;

    @Override
    public Collection<Item> findByUserId(long userId) {
        return items.getOrDefault(userId, Collections.emptyMap()).values();
    }

    @Override
    public Item save(Item item) {
        item.setId(lastId++);
        items.compute(item.getOwnerId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new HashMap<>();
            }
            userItems.put(item.getId(), item);
            return userItems;
        });
        owners.put(item.getId(), item.getOwnerId());
        return item;
    }

    @Override
    public Optional<Item> getById(long itemId) {
        return Optional.ofNullable(items.get(owners.get(itemId)).get(itemId));
    }

    @Override
    public Item patch(Item item) {
        long itemId = item.getId();
        if (items.containsKey(item.getOwnerId()) &&
                items.get(item.getOwnerId()).containsKey(itemId)) {
            Item oldItem = items.get(item.getOwnerId()).get(itemId);
            if (!item.getName().isBlank()) {
                oldItem.setName(item.getName());
            }
            if (!item.getDescription().isBlank()) {
                oldItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                oldItem.setAvailable(item.getAvailable());
            }
            return oldItem;
        }
        throw new EntityNotFoundException("Item not found");
    }

    @Override
    public Collection<Item> itemsSearch(String text) {
        return items.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public Item deleteByUserIdAndItemId(long userId, long itemId) {
        if (items.containsKey(userId)) {
            return items.get(userId).remove(itemId);
        }
        return null;
    }
}
