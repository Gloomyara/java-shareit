package ru.practicum.shareit.booking.state.searcher;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface SearchByState {

    List<Booking> findAllByItemOwnerId(Long ownerId, Pageable pageable);

    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

}
