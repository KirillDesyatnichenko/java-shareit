package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                               LocalDateTime start,
                                                               LocalDateTime end,
                                                               Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByItem_Owner_Id(Long ownerId, Sort sort);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long ownerId,
                                                                   LocalDateTime start,
                                                                   LocalDateTime end,
                                                                   Sort sort);

    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartLessThanOrderByEndDesc(Long itemId,
                                                                                BookingStatus status,
                                                                                LocalDateTime time);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartGreaterThanOrderByStartAsc(Long itemId,
                                                                                    BookingStatus status,
                                                                                    LocalDateTime time);

    List<Booking> findByBooker_IdAndItem_IdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime now);

    List<Booking> findByItem_IdInAndStatusAndStartLessThan(List<Long> itemIds, BookingStatus status, LocalDateTime time);

    List<Booking> findByItem_IdInAndStatusAndStartGreaterThan(List<Long> itemIds, BookingStatus status, LocalDateTime time);
}