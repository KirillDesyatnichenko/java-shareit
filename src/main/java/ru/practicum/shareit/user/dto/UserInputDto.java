package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserInputDto {
    @NotBlank(message = "Имя должно быть заполнено!")
    @Size(max = 200)
    private String name;
    @NotNull(message = "Адрес электронной почты не заполнен!")
    @Email(message = "Некорректный адрес электронной почты!")
    private String email;
}