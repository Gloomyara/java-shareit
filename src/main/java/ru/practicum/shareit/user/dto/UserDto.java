package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Builder
@Data
public class UserDto {

    private Long id;
    @Email
    private String email;
    private String name;
}