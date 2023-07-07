package ru.practicum.shareit.abstraction.mapper;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractModelMapper<I, O, E> implements ModelMapper<I, O, E> {

    @Override
    public List<O> toDto(List<E> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}
