package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@Builder
public class Item {
    private Long id;
    @NotBlank(message = "Название должно быть заполнено!")
    @Size(max = 200)
    private String name;
    @NotBlank(message = "Описание должно быть заполнено!")
    @Size(max = 2000)
    private String description;
    @NotNull(message = "Необходимо указать, доступна ли вещь!")
    private Boolean available;
    private User owner;
    private ItemRequest request;
}