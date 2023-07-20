package ru.practicum.shareit.booking.state;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.exception.UnknownStateException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateFromStringTest {

    @Test
    void whenStateIsExistsReturnState() {
        String str = "ALL";
        State state = State.valueOf(str);
        assertEquals(State.ALL, state);
    }

    @Test
    void whenStateNotExistsAssertThrowsUnknownStateException() {
        String str = "WUT";
        assertThrows(UnknownStateException.class,
                () -> State.fromString(str));
    }
}
