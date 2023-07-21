package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserDtoOut findById(Long id);

    List<UserDtoOut> findAll();

    UserDtoOut save(UserDtoIn userDtoIn);

    UserDtoOut update(UserDtoIn userDtoIn);

    UserDtoOut patch(Long id, Map<String, Object> fields);

    void delete(Long id);

}
