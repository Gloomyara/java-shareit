package ru.practicum.shareit.exceptions.item;

public class UnregisteredBookingException extends IllegalArgumentException {

    public UnregisteredBookingException(String message) {
        super(message);
    }
}
