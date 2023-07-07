package ru.practicum.shareit.item.service;

import ru.practicum.shareit.abstraction.userreference.service.UserReferenceService;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;

public interface ItemService extends UserReferenceService<ItemDtoIn, ItemDtoOut> {

    ItemDtoOut findById(Long itemId, Long userId);

    List<ItemDtoOut> searchByText(String text);

    CommentDtoOut createComment(Long itemId, Long userId, CommentDtoIn commentDtoIn);

}
