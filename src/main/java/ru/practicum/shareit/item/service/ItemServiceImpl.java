package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    @Override
    public Collection<ItemDto> getItems(long userId) {
        userRepository.containsOrElseThrow(userId);
        return repository.findByUserId(userId).stream()
                .map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto patch(long userId, long itemId, ItemDto itemDto) {
        userRepository.containsOrElseThrow(userId);
        Item item = mapper.toObject(userId, itemDto);
        item.setId(itemId);
        return mapper.toDto(repository.patch(item));
    }

    @Override
    public ItemDto getById(long itemId) {
        return mapper.toDto(repository.getById(itemId).orElse(null));
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text.isBlank()) return new ArrayList<>();
        return repository.itemsSearch(text.toLowerCase()).stream()
                .map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        userRepository.containsOrElseThrow(userId);
        return mapper.toDto(repository.save(mapper.toObject(userId, itemDto)));
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        userRepository.containsOrElseThrow(userId);
        repository.deleteByUserIdAndItemId(userId, itemId);
    }
}
