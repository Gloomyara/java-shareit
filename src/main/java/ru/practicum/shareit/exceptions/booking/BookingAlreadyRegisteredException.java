package ru.practicum.shareit.exceptions.booking;

public class BookingAlreadyRegisteredException extends IllegalArgumentException {

    public BookingAlreadyRegisteredException(String message) {
        super(message);
    }
}
