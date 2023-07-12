package ru.practicum.shareit.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() {
        super("Error! Entity not found.");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(Long id, String entityType) {
        super("Error! " + entityType + " id:" + id + " not found.");
    }
}
