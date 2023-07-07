package ru.practicum.shareit.booking.state.searcher;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface SearchByState {

    Sort sort = Sort.by("start").descending();

    List<Booking> findAllByOwnerId(Long ownerId);

    List<Booking> findAllByBookerId(Long userId);

}
