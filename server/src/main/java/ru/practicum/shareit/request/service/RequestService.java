package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;

import java.util.List;

public interface RequestService {

    RequestDtoOut save(RequestDtoIn requestDtoIn, Long authorId);

    RequestDtoOut findById(Long requestId, Long authorId);

    List<RequestDtoOut> findAll(Integer from, Integer limit, Long authorId);

    List<RequestDtoOut> findAllByAuthorId(Long authorId);

}