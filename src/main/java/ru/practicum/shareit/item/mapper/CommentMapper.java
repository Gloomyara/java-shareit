package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper implements ModelMapper<CommentDtoIn, CommentDtoOut, Comment> {

    @Override
    public Comment toEntity(CommentDtoIn dtoIn) {
        if (dtoIn == null) {
            return null;
        }
        return Comment.builder()
                .text(dtoIn.getText())
                .build();
    }

    @Override
    public CommentDtoOut toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDtoOut.builder()
                .authorName(commentAuthorName(comment))
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

    @Override
    public List<CommentDtoOut> toDto(List<Comment> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    private String commentAuthorName(Comment comment) {
        if (comment == null) {
            return null;
        }
        User author = comment.getAuthor();
        if (author == null) {
            return null;
        }
        String name = author.getName();
        if (name == null) {
            return null;
        }
        return name;
    }
}
