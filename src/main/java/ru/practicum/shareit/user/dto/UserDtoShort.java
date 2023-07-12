package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.abstraction.model.DtoOut;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoShort implements DtoOut {

    private Long id;

}
