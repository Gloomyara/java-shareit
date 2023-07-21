package ru.practicum.shareit.exceptions.user;

public class ObjectOwnerException extends RuntimeException {

    public ObjectOwnerException(String message) {
        super(message);
    }

    public ObjectOwnerException(Long id, String objectType) {
        super("Error! User id:" + id + " is not " + objectType + " owner.");
    }
}
