package ru.practicum.shareit.booking.exception;

public class RentTimeConstraintException extends IllegalArgumentException {

    public RentTimeConstraintException() {
        super("Error! Item rent start is after rent end.");
    }
}
