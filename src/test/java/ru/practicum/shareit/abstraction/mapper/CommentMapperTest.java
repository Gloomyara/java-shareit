package ru.practicum.shareit.abstraction.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest extends AbstractModelMapperTest<CommentDtoIn, CommentDtoOut, Comment, CommentMapper> {

    private final Long userId = generator.nextLong();
    private final String userName = generator.nextObject(String.class);
    private final String userEmail = generator.nextObject(String.class);

    private final Long commentId = generator.nextLong();
    private final String commentText = generator.nextObject(String.class);
    private final LocalDateTime commentCreated = generator.nextObject(LocalDateTime.class);

    private final User user = User.builder()
            .id(userId)
            .name(userName)
            .email(userEmail)
            .build();

    private final Comment comment = Comment.builder()
            .id(commentId)
            .text(commentText)
            .author(user)
            .created(commentCreated)
            .build();

    private final CommentDtoIn commentDtoIn = CommentDtoIn.builder()
            .text(commentText)
            .build();

    private final CommentDtoOut commentDtoOut = CommentDtoOut.builder()
            .id(commentId)
            .text(commentText)
            .authorName(userName)
            .created(commentCreated)
            .build();

    protected CommentMapperTest() {
        super(new CommentMapper());
    }

    @Override
    protected Comment getEntity() {
        return comment;
    }

    @Override
    protected CommentDtoIn getDtoIn() {
        return commentDtoIn;
    }

    @Override
    protected CommentDtoOut getDtoOut() {
        return commentDtoOut;
    }

    @Test
    @Override
    void toEntityTest() {
        assertEquals(mapper.toEntity(commentDtoIn).getText(), comment.getText());
    }
}
