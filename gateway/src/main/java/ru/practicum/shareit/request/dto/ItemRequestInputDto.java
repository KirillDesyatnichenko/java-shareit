package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestInputDto {
    @NotBlank(message = "Описание запроса должно быть заполнено!")
    @Size(max = 2000)
    private String description;
}