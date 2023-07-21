package ru.practicum.shareit.abstraction.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemMapperTest extends AbstractModelMapperTest<ItemDtoIn, ItemDtoOut, Item, ItemMapper> {

    private final Long userId = generator.nextLong();
    private final String userName = generator.nextObject(String.class);
    private final String userEmail = generator.nextObject(String.class);

    private final Long requestId = generator.nextLong();
    private final LocalDateTime requestCreated = generator.nextObject(LocalDateTime.class);
    private final String requestDescription = generator.nextObject(String.class);

    private final Long itemId = generator.nextLong();
    private final String itemName = generator.nextObject(String.class);
    private final String itemDescription = generator.nextObject(String.class);
    private final Boolean itemAvailable = generator.nextBoolean();

    private final User user = User.builder()
            .id(userId)
            .name(userName)
            .email(userEmail)
            .build();

    private final Request request = Request.builder()
            .id(requestId)
            .description(requestDescription)
            .created(requestCreated)
            .author(user)
            .build();

    private final Item item = Item.builder()
            .id(itemId)
            .name(itemName)
            .description(itemDescription)
            .available(itemAvailable)
            .owner(user)
            .request(request)
            .build();

    private final ItemDtoIn itemDtoIn = ItemDtoIn.builder()
            .id(itemId)
            .name(itemName)
            .description(itemDescription)
            .available(itemAvailable)
            .build();

    private final ItemDtoOut itemDtoOut = ItemDtoOut.builder()
            .id(itemId)
            .name(itemName)
            .description(itemDescription)
            .available(itemAvailable)
            .requestId(requestId)
            .build();

    private final ItemDtoShort itemDtoShort = ItemDtoShort.builder()
            .id(itemId)
            .name(itemName)
            .description(itemDescription)
            .available(itemAvailable)
            .requestId(requestId)
            .ownerId(userId)
            .build();

    protected ItemMapperTest() {
        super(new ItemMapper());
    }

    @Override
    protected Item getEntity() {
        return item;
    }

    @Override
    protected ItemDtoIn getDtoIn() {
        return itemDtoIn;
    }

    @Override
    protected ItemDtoOut getDtoOut() {
        return itemDtoOut;
    }

    @Test
    void toDtoShort() {
        assertEquals(mapper.toDtoShort(item), itemDtoShort);
    }

    @Test
    void toDtoShortList() {
        assertArrayEquals(mapper.toDtoShort(List.of(item)).toArray(), List.of(itemDtoShort).toArray());
    }
}
