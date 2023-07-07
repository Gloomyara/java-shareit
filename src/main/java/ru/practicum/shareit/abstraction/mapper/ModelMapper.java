package ru.practicum.shareit.abstraction.mapper;

import java.util.List;

public interface ModelMapper<I, O, E> {

    O toDto(E entity);

    List<O> toDto(List<E> entities);

    E dtoToEntity(I dtoIn);

}
