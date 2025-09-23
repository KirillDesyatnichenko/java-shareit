package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    BookingDto createBooking(Long userId, BookingInputDto request);

    BookingDto approveBooking(Long ownerId, Long bookingId, boolean approved);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getBookingsForUser(Long userId, BookingState state);

    List<BookingDto> getBookingsForOwner(Long ownerId, BookingState state);

    Optional<BookingShortDto> getLastBookingForItem(Long itemId);

    Optional<BookingShortDto> getNextBookingForItem(Long itemId);
}