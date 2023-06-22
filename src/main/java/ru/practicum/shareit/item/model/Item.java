package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Builder
@Data
public class Item {

    private Long id;
    private User owner;
    private String name;
    private String description;
    private Boolean available;

}