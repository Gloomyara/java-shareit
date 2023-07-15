package ru.practicum.shareit.abstraction.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.abstraction.model.DtoIn;
import ru.practicum.shareit.abstraction.model.DtoOut;
import ru.practicum.shareit.abstraction.model.EntityClass;
import ru.practicum.shareit.exceptions.JsonUpdateFieldsException;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractService<I extends DtoIn, O extends DtoOut, E extends EntityClass> {

    private final ObjectMapper objectMapper;

    protected E tryUpdateFields(E entity, Map<String, Object> fields) {
        try {
            return objectMapper.updateValue(entity, fields);
        } catch (JsonMappingException e) {
            throw new JsonUpdateFieldsException(
                    String.format("Error! Unable update %s fields", entity.getClass().getSimpleName()));
        }
    }

    public abstract E toEntity(I dtoIn);

    public abstract O toDto(E entity);

    public abstract List<O> toDto(List<E> dtoInList);

}
