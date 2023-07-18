package ru.practicum.shareit.booking.state.searcher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.exceptions.booking.UnknownStateException;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SearchByStateFactory {

    private final Map<String, SearchByState> stateMap;

    public SearchByState searchByState(State status) {
        if (stateMap.containsKey(status.name().toLowerCase())) {
            return stateMap.get(status.name().toLowerCase());
        } else {
            throw new UnknownStateException();
        }
    }
}
