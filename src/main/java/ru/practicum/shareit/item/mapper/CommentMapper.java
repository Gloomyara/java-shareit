package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.AbstractModelMapper;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;

@Component
public class CommentMapper extends AbstractModelMapper<CommentDtoIn, CommentDtoOut, Comment> {

    @Override
    public CommentDtoOut toDto(Comment comment) {
        return CommentDtoOut.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getUser().getName())
                .created(comment.getCreated())
                .build();
    }

    @Override
    public Comment dtoToEntity(CommentDtoIn commentDtoIn) {
        return Comment.builder()
                .text(commentDtoIn.getText())
                .build();
    }

}
