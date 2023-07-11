package ru.practicum.shareit.abstraction.mapper;

import ru.practicum.shareit.abstraction.model.DtoIn;
import ru.practicum.shareit.abstraction.model.DtoOut;

import java.util.List;

public interface ModelMapper<E> {

    DtoOut toDto(E entity);

    E dtoToEntity(DtoIn dtoIn);

    List<? extends DtoOut> toDto(List<E> entities);
}
