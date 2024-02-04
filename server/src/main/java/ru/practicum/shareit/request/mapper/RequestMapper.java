package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.request.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper implements ModelMapper<RequestDtoIn, RequestDtoOut, Request> {

    @Override
    public RequestDtoOut toDto(Request entity) {
        if (entity == null) {
            return null;
        }
        return RequestDtoOut.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .created(entity.getCreated())
                .build();
    }

    @Override
    public Request toEntity(RequestDtoIn dtoIn) {
        if (dtoIn == null) {
            return null;
        }
        return Request.builder()
                .id(dtoIn.getId())
                .description(dtoIn.getDescription())
                .build();
    }

    @Override
    public List<RequestDtoOut> toDto(List<Request> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}