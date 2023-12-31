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
public class UserDtoOut implements DtoOut {

    private Long id;
    private String name;
    private String email;

}
