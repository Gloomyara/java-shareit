package ru.practicum.shareit.abstraction.service;

import ru.practicum.shareit.user.model.User;

import java.util.Map;

class AbstractUserServiceTest extends AbstractServiceTest<User> {

    private final Long userId = generator.nextLong();
    private final String userName = generator.nextObject(String.class);
    private final String userEmail = generator.nextObject(String.class);

    private final String updatedUserName = generator.nextObject(String.class);
    private final String updatedUserEmail = generator.nextObject(String.class);

    @Override
    protected User getEntity() {
        return User.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
    }

    @Override
    protected User getUpdated() {
        return User.builder()
                .id(userId)
                .name(updatedUserName)
                .email(updatedUserEmail)
                .build();
    }

    @Override
    protected Map<String, Object> getFields() {
        return Map.of(
                "name", updatedUserName,
                "email", updatedUserEmail
        );
    }
}
