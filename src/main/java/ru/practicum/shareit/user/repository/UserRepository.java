package ru.practicum.shareit.user.repository;


import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    void containsOrElseThrow(long id);

    Collection<User> findAll();

    User create(User user);

    Optional<User> getById(Long id);

    User patch(Long id, UserDto userDto);

    void delete(Long id);
}