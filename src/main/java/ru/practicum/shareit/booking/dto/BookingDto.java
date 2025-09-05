package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.BookerShortDto;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull(message = "Необходимо указать начало бронирования!")
    private LocalDateTime start;

    @NotNull(message = "Необходимо указать окончание бронирования!")
    private LocalDateTime end;

    @NotNull(message = "Необходимо указать ID вещи!")
    private Long itemId;

    @NotNull(message = "Необходимо указать ID пользователя!")
    private Long bookerId;

    @Size(max = 20)
    @NotNull(message = "Необходимо указать статус бронирования!")
    private BookingStatus status;

    private BookerShortDto booker;

    private ItemShortDto item;
}