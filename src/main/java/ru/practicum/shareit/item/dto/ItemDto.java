package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Название должно быть заполнено!")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "Описание должно быть заполнено!")
    @Size(max = 2000)
    private String description;

    @NotNull(message = "Необходимо указать, доступна ли вещь!")
    private Boolean available;

    private Long requestId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BookingShortDto lastBooking;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BookingShortDto nextBooking;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<CommentDto> comments;
}