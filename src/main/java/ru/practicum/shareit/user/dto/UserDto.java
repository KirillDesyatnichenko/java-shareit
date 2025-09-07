package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@EqualsAndHashCode(of = {"id", "email"})
@AllArgsConstructor
@Builder
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Имя должно быть заполнено!")
    @Size(max = 255)
    private String name;

    @NotNull(message = "Адрес электронной почты не заполнен!")
    @Email(message = "Некорректный адрес электронной почты!")
    private String email;
}