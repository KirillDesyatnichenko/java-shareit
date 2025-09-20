package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingShortDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long bookerId;
}