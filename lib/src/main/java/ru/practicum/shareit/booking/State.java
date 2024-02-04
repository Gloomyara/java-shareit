package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.exception.UnknownStateException;

public enum State {
    ALL("All"),
    CURRENT("CURRENT"),
    FUTURE("FUTURE"),
    PAST("PAST"),
    REJECTED("REJECTED"),
    WAITING("WAITING"),
    UNKNOWN("UNKNOWN");

    private final String stateName;

    State(String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }

    public static State fromString(String state) {
        try {
            return State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException();
        }
    }

    @Override
    public String toString() {
        return "State: " + stateName;
    }
}