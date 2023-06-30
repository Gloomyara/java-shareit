package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) return null;
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item dtoToItem(User user, ItemDto itemDto) {
        if (itemDto == null) return null;
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .owner(user)
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
