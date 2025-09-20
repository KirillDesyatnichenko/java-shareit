package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody BookingInputDto input) {
        log.info("Пользователь {} создал бронирование вещи {}", userId, input.getItemId());
        return ResponseEntity.ok(bookingService.createBooking(userId, input));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam boolean approved) {
        log.info("Владелец {} на бронирование вещи {} поставил статус={}", ownerId, bookingId, approved);
        return ResponseEntity.ok(bookingService.approveBooking(ownerId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        log.info("Пользователь {} получил информацию о бронировании {}", userId, bookingId);
        return ResponseEntity.ok(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookingsForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "ALL") String state) {
        BookingState s = BookingState.from(state);
        log.info("Пользователь {} запрашивает список забронированных вещей с состоянием {}", userId, s);
        return ResponseEntity.ok(bookingService.getBookingsForUser(userId, s));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getBookingsForOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        BookingState s = BookingState.from(state);
        log.info("Собственник {} запрашивает список своих вещей со статусом брони {}", ownerId, s);
        return ResponseEntity.ok(bookingService.getBookingsForOwner(ownerId, s));
    }
}