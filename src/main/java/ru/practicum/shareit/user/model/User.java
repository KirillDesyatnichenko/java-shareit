package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@EqualsAndHashCode(of = {"id", "email"})
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    @NotBlank(message = "Имя должно быть заполнено!")
    @Size(max = 200)
    private String name;
    @NotNull(message = "Адрес электронной почты не заполнен!")
    @Email(message = "Некорректный адрес электронной почты!")
    private String email;
}