package ru.practicum.shareit.booking.state;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FromStringConverter implements Converter<String, State> {
    @Override
    public State convert(String state) {
        try {
            return State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            return State.UNKNOWN;
        }
    }
}