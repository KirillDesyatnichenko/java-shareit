package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.BaseService;
import ru.practicum.shareit.exeption.ForbiddenException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl extends BaseService implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private static final Sort ORDER_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDto createBooking(Long userId, BookingInputDto request) {
        User booker = getOrThrow(userRepository.findById(userId),
                "Пользователь с id " + userId + " не найден");

        Item item = getOrThrow(itemRepository.findById(request.getItemId()),
                "Вещь с id " + request.getItemId() + " не найдена");

        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Владелец не может бронировать свою вещь");
        }

        Booking entity = BookingMapper.toEntity(request, item, booker);
        Booking saved = bookingRepository.save(entity);

        log.info("Создано бронирование {} пользователем {} для вещи {}", saved.getId(), userId, item.getId());
        return BookingMapper.toDto(saved);
    }

    @Override
    public BookingDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = getOrThrow(bookingRepository.findById(bookingId),
                "Бронирование c ID " + bookingId + " не найдено");

        Long ownerOfItem = booking.getItem().getOwner().getId();
        if (!ownerOfItem.equals(ownerId)) {
            throw new ForbiddenException("Только владелец вещи может изменять статус бронирования");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус уже был установлен: " + booking.getStatus());
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);

        log.info("Бронирование {} теперь в статусе {}", saved.getId(), saved.getStatus());
        return BookingMapper.toDto(saved);
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = getOrThrow(bookingRepository.findById(bookingId),
                "Бронирование c ID " + bookingId + " не найдено");

        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
            throw new ForbiddenException("Нет доступа к бронированию: " +
                    "пользователь с ID " + userId + " не является владельцем вещи и не является её арендатором");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsForUser(Long userId, BookingState state) {
        getOrThrow(userRepository.findById(userId),
                "Пользователь с id " + userId + " не найден");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> list;
        switch (state) {
            case CURRENT -> list = bookingRepository
                    .findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, now, now, ORDER_BY_START_DESC);
            case PAST -> list = bookingRepository
                    .findByBooker_IdAndEndIsBefore(userId, now, ORDER_BY_START_DESC);
            case FUTURE -> list = bookingRepository
                    .findByBooker_IdAndStartIsAfter(userId, now, ORDER_BY_START_DESC);
            case WAITING -> list = bookingRepository
                    .findByBooker_IdAndStatus(userId, BookingStatus.WAITING, ORDER_BY_START_DESC);
            case REJECTED -> list = bookingRepository
                    .findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, ORDER_BY_START_DESC);
            case ALL -> list = bookingRepository
                    .findByBooker_Id(userId, ORDER_BY_START_DESC);
            default -> throw new IllegalArgumentException("Неизвестное состояние: " + state);
        }

        return list.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsForOwner(Long ownerId, BookingState state) {
        getOrThrow(userRepository.findById(ownerId),
                "Пользователь с id " + ownerId + " не найден");

        LocalDateTime now = LocalDateTime.now();

        List<Booking> list;
        switch (state) {
            case CURRENT -> list = bookingRepository
                    .findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(ownerId, now, now, ORDER_BY_START_DESC);
            case PAST -> list = bookingRepository
                    .findByItem_Owner_IdAndEndIsBefore(ownerId, now, ORDER_BY_START_DESC);
            case FUTURE -> list = bookingRepository
                    .findByItem_Owner_IdAndStartIsAfter(ownerId, now, ORDER_BY_START_DESC);
            case WAITING -> list = bookingRepository
                    .findByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING, ORDER_BY_START_DESC);
            case REJECTED -> list = bookingRepository
                    .findByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED, ORDER_BY_START_DESC);
            case ALL -> list = bookingRepository
                    .findByItem_Owner_Id(ownerId, ORDER_BY_START_DESC);
            default -> throw new IllegalArgumentException("Неизвестное состояние: " + state);
        }

        return list.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BookingShortDto> getLastBookingForItem(Long itemId) {
        return bookingRepository
                .findFirstByItem_IdAndStatusAndStartLessThanOrderByEndDesc(
                        itemId, BookingStatus.APPROVED, LocalDateTime.now()
                )
                .map(BookingMapper::toShortDto);
    }

    @Override
    public Optional<BookingShortDto> getNextBookingForItem(Long itemId) {
        return bookingRepository
                .findFirstByItem_IdAndStatusAndStartGreaterThanOrderByStartAsc(
                        itemId, BookingStatus.APPROVED, LocalDateTime.now()
                )
                .map(BookingMapper::toShortDto);
    }
}