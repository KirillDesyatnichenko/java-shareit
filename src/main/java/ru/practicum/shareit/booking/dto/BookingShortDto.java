package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingShortDto {
    @NotNull(message = "Необходимо указать ID вещи!")
    private Long id;

    @NotNull(message = "Необходимо указать дату начала бронирования!")
    private LocalDateTime start;

    @NotNull(message = "Необходимо указать дату окончания бронирования!")
    private LocalDateTime end;

    @NotNull(message = "Необходимо указать ID пользователя!")
    private Long bookerId;
}