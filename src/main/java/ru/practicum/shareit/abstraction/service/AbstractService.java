package ru.practicum.shareit.abstraction.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.abstraction.model.DtoIn;
import ru.practicum.shareit.abstraction.model.DtoOut;
import ru.practicum.shareit.abstraction.model.Identified;
import ru.practicum.shareit.exceptions.JsonUpdateFieldsException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractService<E extends Identified> {

    protected final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    protected E tryUpdateFields(E entity, Map<String, Object> fields) {
        try {
            return objectMapper.updateValue(entity, fields);
        } catch (JsonMappingException e) {
            throw new JsonUpdateFieldsException(
                    String.format("Error! Unable update %s fields", entity.getClass().getSimpleName()));
        }
    }

    public abstract E dtoToEntity(DtoIn in);

    public abstract DtoOut toDto(E e);

    public abstract List<? extends DtoOut> toDto(List<E> listIn);
}
