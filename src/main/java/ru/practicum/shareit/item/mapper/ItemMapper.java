package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemShort;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper implements ModelMapper<ItemDtoIn, ItemDtoOut, Item> {

    @Override
    public Item toEntity(ItemDtoIn dtoIn) {
        if (dtoIn == null) {
            return null;
        }

        return Item.builder()
                .id(dtoIn.getId())
                .name(dtoIn.getName())
                .description(dtoIn.getDescription())
                .available(dtoIn.getAvailable())
                .build();
    }

    @Override
    public List<ItemDtoOut> toDto(List<Item> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemDtoOut toDto(Item item) {
        if (item == null) {
            return null;
        }

        return ItemDtoOut.builder()
                .requestId(itemRequestId(item))
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public ItemDtoShort toDtoShort(Item item) {
        if (item == null) {
            return null;
        }

        ItemDtoShort itemDtoShort = ItemDtoShort.builder()
                .requestId(itemRequestId(item))
                .ownerId(itemOwnerId(item))
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .build();
        if (item.getAvailable() != null) {
            itemDtoShort.setAvailable(item.getAvailable());
        }
        return itemDtoShort;
    }
    public List<ItemDtoShort> toDtoShort(List<Item> items) {
        if (items == null) {
            return null;
        }
        return items.stream().map(this::toDtoShort).collect(Collectors.toList());
    }

    public ItemDtoOut shortToDto(ItemShort item) {
        if (item == null) {
            return null;
        }

        return ItemDtoOut.builder()
                .requestId(itemRequestId(item))
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public List<ItemDtoOut> shortToDto(List<ItemShort> items) {
        if (items == null) {
            return null;
        }
        return items.stream().map(this::shortToDto).collect(Collectors.toList());
    }

    private Long itemRequestId(Item item) {
        if (item == null) {
            return null;
        }
        Request request = item.getRequest();
        if (request == null) {
            return null;
        }
        Long id = request.getId();
        if (id == null) {
            return null;
        }
        return id;
    }

    private Long itemRequestId(ItemShort item) {
        if (item == null) {
            return null;
        }
        Request request = item.getRequest();
        if (request == null) {
            return null;
        }
        Long id = request.getId();
        if (id == null) {
            return null;
        }
        return id;
    }

    private Long itemOwnerId(Item item) {
        if (item == null) {
            return null;
        }
        User owner = item.getOwner();
        if (owner == null) {
            return null;
        }
        Long id = owner.getId();
        if (id == null) {
            return null;
        }
        return id;
    }
}
