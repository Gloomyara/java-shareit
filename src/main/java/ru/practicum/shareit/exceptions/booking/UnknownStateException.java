package ru.practicum.shareit.exceptions.booking;

public class UnknownStateException extends RuntimeException {

    public UnknownStateException() {
        super("Unknown state: UNSUPPORTED_STATUS");
    }

    public UnknownStateException(String message) {
        super(message);
    }
}
