package ru.practicum.shareit.booking.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StateFromStringTest {

    @Test
    void whenStateIsExistsReturnState() {
        String str = "ALL";
        State state = State.valueOf(str);
        assertEquals(State.ALL, state);
    }

    @Test
    void whenStateNotExistsReturnUnknownState() {
        String str = "RUN";
        State state = State.fromString(str);
        assertEquals(State.UNKNOWN, state);
    }
}
