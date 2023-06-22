package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.model.ItemMapper.dtoToItem;
import static ru.practicum.shareit.item.model.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> getItems(long userId) {
        userRepository.containsOrElseThrow(userId);
        return repository.findByUserId(userId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto patch(long userId, long itemId, ItemDto itemDto) {
        Optional<User> optOwner = userRepository.getById(userId);
        if (optOwner.isEmpty()) {
            log.warn("User with Id: {} not found", userId);
            throw new EntityNotFoundException("User with Id: " + userId + " not found");
        }
        Item item = dtoToItem(optOwner.get(), itemDto);
        item.setId(itemId);
        return toItemDto(repository.patch(item));
    }

    @Override
    public ItemDto getById(long itemId) {
        return toItemDto(repository.getById(itemId).orElse(null));
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text.isBlank()) return new ArrayList<>();
        return repository.itemsSearch(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        Optional<User> optOwner = userRepository.getById(userId);
        if (optOwner.isEmpty()) {
            log.warn("User with Id: {} not found", userId);
            throw new EntityNotFoundException("User with Id: " + userId + " not found");
        }
        userRepository.containsOrElseThrow(userId);
        return toItemDto(repository.save(dtoToItem(optOwner.get(), itemDto)));
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        userRepository.containsOrElseThrow(userId);
        repository.deleteByUserIdAndItemId(userId, itemId);
    }
}
