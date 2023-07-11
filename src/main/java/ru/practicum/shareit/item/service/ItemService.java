package ru.practicum.shareit.item.service;

import ru.practicum.shareit.abstraction.model.DtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;
import java.util.Map;

public interface ItemService {

    ItemDtoOut findById(Long itemId, Long userId);

    ItemDtoOut findById(Long objectId);

    List<ItemDtoOut> findAllByOwnerId(Long userId);

    ItemDtoOut create(DtoIn in, Long userId);

    ItemDtoOut update(DtoIn in, Long userId);

    ItemDtoOut patch(Long id, Map<String, Object> fields, Long userId);

    List<ItemDtoOut> searchByText(String text);

    CommentDtoOut createComment(Long itemId, Long userId, CommentDtoIn commentDtoIn);

}
