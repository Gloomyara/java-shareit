package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getAllUsers();

    User saveUser(UserDto userDto);

    User patch(Long id, UserDto userDto);

    void delete(Long id);
}