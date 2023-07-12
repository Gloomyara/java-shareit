package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.abstraction.model.DtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper implements ModelMapper<Booking> {

    @Override
    public BookingDtoOut toDto(Booking booking) {
        return BookingDtoOut.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    @Override
    public List<BookingDtoOut> toDto(List<Booking> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Booking dtoToEntity(DtoIn in) {
        BookingDtoIn bookingDtoIn = (BookingDtoIn) in;
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
