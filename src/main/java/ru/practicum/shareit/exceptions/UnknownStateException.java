package ru.practicum.shareit.exceptions;

public class UnknownStateException extends RuntimeException {

    public UnknownStateException() {
        super("Unknown state: UNSUPPORTED_STATUS");
    }

    public UnknownStateException(String message) {
        super(message);
    }
}
