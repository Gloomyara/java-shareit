package ru.practicum.shareit.user.repository;


import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    void containsOrElseThrow(long id);

    Collection<User> findAll();

    User create(User user);

    User patch(User user);

    User delete(Long id);
}