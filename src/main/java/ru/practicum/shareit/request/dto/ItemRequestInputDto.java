package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestInputDto {
    @NotBlank(message = "Описание запроса должно быть заполнено!")
    @Size(max = 2000)
    private String description;

    @NotNull(message = "Необходимо указать ID автора запроса!")
    private Long requestorId;

    private LocalDateTime created;
}
