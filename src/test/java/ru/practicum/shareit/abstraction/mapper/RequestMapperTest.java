package ru.practicum.shareit.abstraction.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestMapperTest extends AbstractModelMapperTest<RequestDtoIn, RequestDtoOut, Request, RequestMapper> {

    private final Long userId = generator.nextLong();
    private final String userName = generator.nextObject(String.class);
    private final String userEmail = generator.nextObject(String.class);

    private final Long requestId = generator.nextLong();
    private final LocalDateTime requestCreated = generator.nextObject(LocalDateTime.class);
    private final String requestDescription = generator.nextObject(String.class);

    private final User user = User.builder()
            .id(userId)
            .name(userName)
            .email(userEmail)
            .build();

    private final Request request = Request.builder()
            .id(requestId)
            .description(requestDescription)
            .created(requestCreated)
            .author(user)
            .build();

    private final RequestDtoIn requestDtoIn = RequestDtoIn.builder()
            .id(requestId)
            .description(requestDescription)
            .build();

    private final RequestDtoOut requestDtoOut = RequestDtoOut.builder()
            .id(requestId)
            .description(requestDescription)
            .created(requestCreated)
            .build();

    protected RequestMapperTest() {
        super(new RequestMapper());
    }

    @Override
    protected Request getEntity() {
        return request;
    }

    @Override
    protected RequestDtoIn getDtoIn() {
        return requestDtoIn;
    }

    @Override
    protected RequestDtoOut getDtoOut() {
        return requestDtoOut;
    }

    @Test
    @Override
    void toEntityTest() {
        Request r = mapper.toEntity(getDtoIn());
        r.setCreated(requestCreated);
        assertEquals(getEntity(), r);
    }
}
