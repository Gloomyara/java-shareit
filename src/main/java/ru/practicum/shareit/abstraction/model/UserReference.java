package ru.practicum.shareit.abstraction.model;

import ru.practicum.shareit.user.model.User;

public interface UserReference extends Identified {

    User getUser();

    void setUser(User user);

}
