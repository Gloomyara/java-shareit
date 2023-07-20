package ru.practicum.shareit.item.service;
import ru.practicum.shareit.item.CommentDtoIn;
import ru.practicum.shareit.item.ItemDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;
import java.util.Map;

public interface ItemService {

    ItemDtoOut findById(Long itemId, Long ownerId);

    List<ItemDtoOut> findAllByOwnerId(Integer from, Integer limit, Long ownerId);

    ItemDtoOut save(ItemDtoIn itemDtoIn, Long ownerId);

    ItemDtoOut update(ItemDtoIn itemDtoIn, Long ownerId);

    ItemDtoOut patch(Long itemId, Map<String, Object> fields, Long ownerId);

    List<ItemDtoOut> searchByNameOrDescription(Integer from, Integer limit, String text);

    CommentDtoOut createComment(Long itemId, Long authorId, CommentDtoIn dtoIn);

}
