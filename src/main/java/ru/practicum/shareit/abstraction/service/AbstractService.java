package ru.practicum.shareit.abstraction.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.abstraction.model.Identified;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exceptions.JsonUpdateFieldsException;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractService<I, O, E extends Identified> {

    private final ModelMapper<I, O, E> mapper;
    protected final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    protected E tryUpdateFields(E entity, Map<String, Object> updatedFields) {
        try {
            return objectMapper.updateValue(entity, updatedFields);
        } catch (JsonMappingException e) {
            throw new JsonUpdateFieldsException(
                    String.format("Error! Unable update %s fields", entity.getClass().getSimpleName()));
        }
    }

    public E dtoToEntity(I in) {
        return mapper.dtoToEntity(in);
    }

    public O toDto(E e) {
        return mapper.toDto(e);
    }

    public List<O> toDto(List<E> listIn) {
        return mapper.toDto(listIn);
    }
}
