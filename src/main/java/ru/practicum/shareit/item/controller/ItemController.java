package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

import static ru.practicum.shareit.util.UtilConstants.OWNER_ID_HEADER;

public interface ItemController {

    @GetMapping
    List<ItemDtoOut> findAllByUserId(@RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId);

    @GetMapping("search")
    List<ItemDtoOut> searchByText(@RequestParam String text);

    @GetMapping("{id}")
    ItemDtoOut findById(@PathVariable("id") @Positive Long itemId,
                        @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId);

    @PostMapping
    ItemDtoOut post(@Valid @RequestBody ItemDtoIn itemDtoIn,
                    @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId);

    @PutMapping
    ItemDtoOut put(@Valid @RequestBody ItemDtoIn itemDtoIn,
                   @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId);

    @PatchMapping("{id}")
    ItemDtoOut patch(@PathVariable("id") @Positive Long itemId,
                     @RequestBody Map<String, Object> fields,
                     @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId);

    @PostMapping("{id}/comment")
    CommentDtoOut postComment(@PathVariable("id") Long itemId,
                              @RequestHeader(value = OWNER_ID_HEADER) @Positive Long userId,
                              @Valid @RequestBody CommentDtoIn commentDtoIn);

}
