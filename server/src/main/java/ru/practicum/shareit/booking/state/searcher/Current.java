package ru.practicum.shareit.booking.state.searcher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Current implements SearchByState {

    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findAllByItemOwnerId(Long itemOwnerId, Pageable pageable) {
        return bookingRepository.findAllByItemOwnerIdWithTimestampsBetweenStartAndEnd(itemOwnerId, pageable).toList();
    }

    @Override
    public List<Booking> findAllByBookerId(Long bookerId, Pageable pageable) {
        return bookingRepository.findAllByBookerIdWithTimestampsBetweenStartAndEnd(bookerId, pageable).toList();
    }
}
