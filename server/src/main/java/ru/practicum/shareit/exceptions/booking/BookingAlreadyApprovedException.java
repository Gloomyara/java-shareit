package ru.practicum.shareit.exceptions.booking;

public class BookingAlreadyApprovedException extends IllegalArgumentException {

    public BookingAlreadyApprovedException(String message) {
        super(message);
    }
}
