package ru.practicum.shareit.exceptions.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Error! User id:" + id + "not found.");
    }
}
