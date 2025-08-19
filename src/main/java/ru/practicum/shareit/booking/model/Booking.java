package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@Builder
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    @NotNull(message = "Необходимо указать вещ для бронирования!")
    private Item item;
    @NotNull(message = "Необходимо указать пользователя!")
    private User booker;
    private BookingStatus status;
}