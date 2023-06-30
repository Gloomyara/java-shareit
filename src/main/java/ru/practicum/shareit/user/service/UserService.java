package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAllUsers();

    UserDto saveUser(UserDto userDto);

    UserDto getById(Long id);

    UserDto patch(Long id, UserDto userDto);

    void delete(Long id);
}