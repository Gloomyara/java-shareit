package ru.practicum.shareit.exceptions.item;

public class ItemNotAvailableException extends IllegalArgumentException {

    public ItemNotAvailableException(String message) {
        super(message);
    }
}
