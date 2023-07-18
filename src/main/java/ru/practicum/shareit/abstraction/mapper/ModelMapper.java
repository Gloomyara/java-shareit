package ru.practicum.shareit.abstraction.mapper;

import ru.practicum.shareit.abstraction.model.DtoIn;
import ru.practicum.shareit.abstraction.model.DtoOut;
import ru.practicum.shareit.abstraction.model.EntityClass;

import java.util.List;

public interface ModelMapper<I extends DtoIn, O extends DtoOut, E extends EntityClass> {

    O toDto(E entity);

    E toEntity(I dtoIn);

    List<O> toDto(List<E> entities);

}
