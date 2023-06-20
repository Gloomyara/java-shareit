package ru.practicum.shareit.exceptions;

public class EntityAlreadyExistException extends RuntimeException {
    public EntityAlreadyExistException(String s) {
        super(s);
    }
}
