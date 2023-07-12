package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.abstraction.model.DtoIn;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper implements ModelMapper<Item> {

    @Override
    public ItemDtoOut toDto(Item item) {
        return ItemDtoOut.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    @Override
    public Item dtoToEntity(DtoIn in) {
        ItemDtoIn itemDtoIn = (ItemDtoIn) in;
        return Item.builder()
                .id(itemDtoIn.getId())
                .name(itemDtoIn.getName())
                .description(itemDtoIn.getDescription())
                .available(itemDtoIn.getAvailable())
                .build();
    }

    @Override
    public List<ItemDtoOut> toDto(List<Item> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public ItemDtoShort toDtoShort(Item item) {
        return ItemDtoShort.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }
}
