package ru.practicum.shareit.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Long id, String entityType) {
        super("Error! " + entityType + " id:" + id + " not found.");
    }
}
