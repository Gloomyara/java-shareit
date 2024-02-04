package ru.practicum.shareit.abstraction.mapper;

import ru.practicum.shareit.abstraction.model.DtoOut;
import ru.practicum.shareit.abstraction.model.EntityClass;
import ru.practicum.shareit.dto.DtoIn;

import java.util.List;

public interface ModelMapper<I extends DtoIn, O extends DtoOut, E extends EntityClass> {

    O toDto(E entity);

    E toEntity(I dtoIn);

    List<O> toDto(List<E> entities);

}
