package ru.practicum.shareit.abstraction.service;

import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;
import java.util.Map;

class AbstractRequestServiceTest extends AbstractServiceTest<Request> {

    private final Long requestId = generator.nextLong();
    private final String requestDescription = generator.nextObject(String.class);
    private final LocalDateTime requestCreated = generator.nextObject(LocalDateTime.class);

    private final String updatedRequestDescription = generator.nextObject(String.class);
    private final LocalDateTime updatedRequestCreated = generator.nextObject(LocalDateTime.class);

    @Override
    protected Request getEntity() {
        return Request.builder()
                .id(requestId)
                .description(requestDescription)
                .created(requestCreated)
                .build();
    }

    @Override
    protected Request getUpdated() {
        return Request.builder()
                .id(requestId)
                .description(updatedRequestDescription)
                .created(updatedRequestCreated)
                .build();
    }

    @Override
    protected Map<String, Object> getFields() {
        return Map.of(
                "description", updatedRequestDescription,
                "created", updatedRequestCreated.toString()
        );
    }
}
