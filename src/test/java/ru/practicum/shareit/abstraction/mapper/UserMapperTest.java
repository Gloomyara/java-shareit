package ru.practicum.shareit.abstraction.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest extends AbstractModelMapperTest<UserDtoIn, UserDtoOut, User, UserMapper> {

    private final Long userId = generator.nextLong();
    private final String userName = generator.nextObject(String.class);
    private final String userEmail = generator.nextObject(String.class);

    private final User user = User.builder()
            .id(userId)
            .name(userName)
            .email(userEmail)
            .build();

    private final UserDtoIn userDtoIn = UserDtoIn.builder()
            .id(userId)
            .name(userName)
            .email(userEmail)
            .build();

    private final UserDtoOut userDtoOut = UserDtoOut.builder()
            .id(userId)
            .name(userName)
            .email(userEmail)
            .build();

    private final UserDtoShort userDtoShort = UserDtoShort.builder()
            .id(userId)
            .build();

    protected UserMapperTest() {
        super(new UserMapper());
    }

    @Override
    protected User getEntity() {
        return user;
    }

    @Override
    protected UserDtoIn getDtoIn() {
        return userDtoIn;
    }

    @Override
    protected UserDtoOut getDtoOut() {
        return userDtoOut;
    }

    @Test
    void toDtoShort() {
        assertEquals(mapper.toDtoShort(user), userDtoShort);
    }
}
