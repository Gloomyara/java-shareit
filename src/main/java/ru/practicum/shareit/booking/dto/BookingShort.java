package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.abstraction.model.DtoOut;

public interface BookingShort extends DtoOut {

    Long getId();

    Long getBookerId();

    Long getItemId();

}
