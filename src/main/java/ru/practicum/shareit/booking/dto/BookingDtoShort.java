package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.abstraction.model.Identified;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoShort implements Identified {

    private Long id;
    private Long bookerId;

}
