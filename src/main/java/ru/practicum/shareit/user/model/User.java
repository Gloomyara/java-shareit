package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class User {
    private Long id;
    @Email
    @NotNull
    private String email;
    @NotBlank
    private String name;

}