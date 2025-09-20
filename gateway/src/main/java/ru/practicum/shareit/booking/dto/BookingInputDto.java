package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingInputDto {
    @NotNull(message = "Необходимо указать ID вещи!")
    private Long itemId;

    @NotNull(message = "Необходимо указать дату начала бронирования!")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом!")
    private LocalDateTime start;

    @NotNull(message = "Необходимо указать дату окончания бронирования!")
    @Future(message = "Дата окончания бронирования должна быть в будущем!")
    private LocalDateTime end;
}
