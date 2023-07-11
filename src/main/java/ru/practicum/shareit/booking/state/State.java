package ru.practicum.shareit.booking.state;

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
            return State.UNKNOWN;
        }
    }

    @Override
    public String toString() {
        return "State: " + stateName;
    }
}