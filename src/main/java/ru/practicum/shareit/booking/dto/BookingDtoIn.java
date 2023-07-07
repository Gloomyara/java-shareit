package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.abstraction.model.Identified;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

import static ru.practicum.shareit.util.UtilConstants.DATE_TIME_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoIn implements Identified {

    @Positive
    private Long id;

    @NotNull
    @FutureOrPresent
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime start;

    @NotNull
    @Future
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime end;

    @NotNull
    @Positive
    private Long itemId;

}
