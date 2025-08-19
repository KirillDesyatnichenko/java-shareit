package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@Builder
public class ItemRequest {
    private Long id;
    @NotBlank(message = "Описание запроса должно быть заполнено!")
    @Size(max = 2000)
    private String description;
    @NotNull(message = "Необходимо указать автора запроса!")
    private User requestor;
    private LocalDateTime created;
}
