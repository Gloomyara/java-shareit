package ru.practicum.shareit.booking.exception;

public class UnknownStateException extends RuntimeException {

    public UnknownStateException() {
        super("Unknown state: UNSUPPORTED_STATUS");
    }

}
