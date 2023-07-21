package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.abstraction.model.DtoOut;

public interface BookingShort extends DtoOut {

    Long getId();

    void setId(Long id);

    Long getBookerId();

    void setBookerId(Long bookerId);

    Long getItemId();

    void setItemId(Long itemId);

}
