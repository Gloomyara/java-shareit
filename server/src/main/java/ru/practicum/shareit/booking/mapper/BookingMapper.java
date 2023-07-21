package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper implements ModelMapper<BookingDtoIn, BookingDtoOut, Booking> {

    @Override
    public BookingDtoOut toDto(Booking entity) {
        if (entity == null) {
            return null;
        }
        return BookingDtoOut.builder()
                .id(entity.getId())
                .start(entity.getStart())
                .end(entity.getEnd())
                .booker(userToUserDtoShort(entity.getBooker()))
                .status(entity.getStatus())
                .build();
    }

    @Override
    public Booking toEntity(BookingDtoIn dtoIn) {
        if (dtoIn == null) {
            return null;
        }
        return Booking.builder()
                .id(dtoIn.getId())
                .start(dtoIn.getStart())
                .end(dtoIn.getEnd())
                .build();
    }

    @Override
    public List<BookingDtoOut> toDto(List<Booking> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public BookingDtoShort toDtoShort(BookingShort booking) {
        if (booking == null) {
            return null;
        }
        return BookingDtoShort.builder()
                .id(booking.getId())
                .bookerId(booking.getBookerId())
                .build();
    }

    protected UserDtoShort userToUserDtoShort(User user) {
        if (user == null) {
            return null;
        }
        return UserDtoShort.builder()
                .id(user.getId())
                .build();
    }
}
