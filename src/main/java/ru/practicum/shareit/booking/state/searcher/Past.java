package ru.practicum.shareit.booking.state.searcher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Past implements SearchByState {

    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findAllByOwnerId(Long ownerId) {
        return bookingRepository.findAllByOwnerIdWhereEndInPast(ownerId, sort);
    }

    @Override
    public List<Booking> findAllByBookerId(Long userId) {
        return bookingRepository.findAllByUserIdWhereEndInPast(userId, sort);
    }
}
