package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.abstraction.model.DtoIn;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper implements ModelMapper<Comment> {

    @Override
    public CommentDtoOut toDto(Comment comment) {
        return CommentDtoOut.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    @Override
    public Comment dtoToEntity(DtoIn in) {
        CommentDtoIn commentDtoIn = (CommentDtoIn) in;
        return Comment.builder()
                .text(commentDtoIn.getText())
                .build();
    }

    @Override
    public List<CommentDtoOut> toDto(List<Comment> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}
