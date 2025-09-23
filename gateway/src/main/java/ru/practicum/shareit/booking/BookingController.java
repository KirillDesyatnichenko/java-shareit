package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingState;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookingsForUser(@RequestHeader("X-Sharer-User-Id") long userId,
			                                  @RequestParam(name = "state", defaultValue = "all") String stateParam,
			                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Неизвестный статус: " + stateParam));

		log.info("Получение списка бронирований со статусом {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookingsForUser(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			                                    @RequestBody @Valid BookingInputDto inputDto) {
		log.info("Пользователь {} создал бронирование вещи {}", userId, inputDto);
		return bookingClient.createBooking(userId, inputDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			                                 @PathVariable Long bookingId) {
		log.info("Пользователь {} получил информацию о бронировании {}", userId, bookingId);
		return bookingClient.getBooking(userId, bookingId);
	}

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam boolean approved) {
        log.info("Владелец {} на бронирование вещи {} поставил статус={}", ownerId, bookingId, approved);
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный статус: " + stateParam));

        log.info("Получение списка бронирований со статусом {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getBookingsForOwner(ownerId, state, from, size);
    }
}
