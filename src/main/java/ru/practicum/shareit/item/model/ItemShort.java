package ru.practicum.shareit.item.model;

import ru.practicum.shareit.abstraction.model.EntityClass;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface ItemShort extends EntityClass {
    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    Boolean getAvailable();

    void setAvailable(Boolean available);

    Long getOwnerId();

    void setOwnerId(Long ownerId);

    List<Comment> getComments();

    void setComments(List<Comment> comments);

    Request getRequest();

    void setRequest(Request request);
}
