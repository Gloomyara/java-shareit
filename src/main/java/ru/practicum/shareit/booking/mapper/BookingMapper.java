package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.AbstractModelMapper;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Component
@RequiredArgsConstructor
public class BookingMapper extends AbstractModelMapper<BookingDtoIn, BookingDtoOut, Booking> {

    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    public BookingDtoOut toDto(Booking booking) {
        return BookingDtoOut.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(userMapper.toDtoShort(booking.getUser()))
                .item(itemMapper.toDtoShort(booking.getItem()))
                .status(booking.getStatus())
                .build();
    }

    @Override
    public Booking dtoToEntity(BookingDtoIn bookingDtoIn) {
        return Booking.builder()
                .id(bookingDtoIn.getId())
                .start(bookingDtoIn.getStart())
                .end(bookingDtoIn.getEnd())
                .build();
    }

    public BookingDtoShort toDtoShort(BookingShort booking) {
        return BookingDtoShort.builder()
                .id(booking.getId())
                .bookerId(booking.getBookerId())
                .build();
    }
}
