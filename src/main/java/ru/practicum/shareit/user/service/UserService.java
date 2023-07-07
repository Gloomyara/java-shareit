package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserDto findById(Long id);

    List<UserDto> findAll();

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto);

    UserDto patch(Long id, Map<String, Object> newFields);

    void delete(Long id);

}
