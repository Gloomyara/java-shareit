package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.abstraction.model.DtoOut;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoShort implements DtoOut {

    private Long id;
    private Long bookerId;

}
