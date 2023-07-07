package ru.practicum.shareit.exceptions.booking;

public class RentTimeConstraintException extends IllegalArgumentException {

    public RentTimeConstraintException(String message) {
        super(message);
    }
}
