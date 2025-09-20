package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner1;
    private User owner2;
    private User booker1;
    private Item item1;
    private Item item2;
    private Booking pastBooking;
    private Booking currentBooking;
    private Booking futureBooking;
    private Booking waitingBooking;
    private Booking rejectedBooking;
    private Booking approvedBooking;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner1 = new User();
        owner1.setName("Owner1");
        owner1.setEmail("owner1@mail.ru");
        owner1 = userRepository.save(owner1);

        owner2 = new User();
        owner2.setName("Owner2");
        owner2.setEmail("owner2@mail.ru");
        owner2 = userRepository.save(owner2);

        booker1 = new User();
        booker1.setName("Booker1");
        booker1.setEmail("booker1@mail.ru");
        booker1 = userRepository.save(booker1);

        item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("Электрическая");
        item1.setAvailable(true);
        item1.setOwner(owner1);
        item1 = itemRepository.save(item1);

        item2 = new Item();
        item2.setName("Сап доска");
        item2.setDescription("Двухместная");
        item2.setAvailable(true);
        item2.setOwner(owner2);
        item2 = itemRepository.save(item2);

        LocalDateTime now = LocalDateTime.now();

        pastBooking = Booking.builder()
                .start(now.minusDays(10))
                .end(now.minusDays(5))
                .item(item1)
                .booker(booker1)
                .status(BookingStatus.APPROVED)
                .build();
        pastBooking = bookingRepository.save(pastBooking);

        currentBooking = Booking.builder()
                .start(now.minusHours(1))
                .end(now.plusHours(1))
                .item(item1)
                .booker(booker1)
                .status(BookingStatus.APPROVED)
                .build();
        currentBooking = bookingRepository.save(currentBooking);

        futureBooking = Booking.builder()
                .start(now.plusDays(5))
                .end(now.plusDays(6))
                .item(item1)
                .booker(booker1)
                .status(BookingStatus.APPROVED)
                .build();
        futureBooking = bookingRepository.save(futureBooking);

        waitingBooking = Booking.builder()
                .start(now.plusDays(2))
                .end(now.plusDays(3))
                .item(item2)
                .booker(booker1)
                .status(BookingStatus.WAITING)
                .build();
        waitingBooking = bookingRepository.save(waitingBooking);

        rejectedBooking = Booking.builder()
                .start(now.minusDays(3))
                .end(now.minusDays(2))
                .item(item2)
                .booker(booker1)
                .status(BookingStatus.REJECTED)
                .build();
        rejectedBooking = bookingRepository.save(rejectedBooking);

        approvedBooking = Booking.builder()
                .start(now.plusDays(7))
                .end(now.plusDays(8))
                .item(item2)
                .booker(booker1)
                .status(BookingStatus.APPROVED)
                .build();
        approvedBooking = bookingRepository.save(approvedBooking);
    }

    @Test
    void findByBooker_Id_allCurrentPastFutureAndByStatus() {
        List<Booking> all = bookingRepository.findByBooker_Id(booker1.getId(), org.springframework.data.domain.Sort.by("start").descending());
        assertNotNull(all);
        assertEquals(6, all.size());

        List<Booking> past = bookingRepository.findByBooker_IdAndEndIsBefore(booker1.getId(), LocalDateTime.now(), org.springframework.data.domain.Sort.by("start").descending());
        assertNotNull(past);
        assertTrue(past.stream().allMatch(b -> b.getEnd().isBefore(LocalDateTime.now())));

        List<Booking> future = bookingRepository.findByBooker_IdAndStartIsAfter(booker1.getId(), LocalDateTime.now(), org.springframework.data.domain.Sort.by("start").descending());
        assertNotNull(future);
        assertTrue(future.stream().allMatch(b -> b.getStart().isAfter(LocalDateTime.now())));

        List<Booking> current = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(booker1.getId(), LocalDateTime.now(), LocalDateTime.now(), org.springframework.data.domain.Sort.by("start").descending());
        assertNotNull(current);
        assertEquals(1, current.size());
        assertEquals(currentBooking.getId(), current.get(0).getId());

        List<Booking> waiting = bookingRepository.findByBooker_IdAndStatus(booker1.getId(), BookingStatus.WAITING, org.springframework.data.domain.Sort.by("start").descending());
        assertNotNull(waiting);
        assertEquals(1, waiting.size());
        assertEquals(waitingBooking.getId(), waiting.get(0).getId());
    }

    @Test
    void findByItem_Owner_Id_queries() {
        List<Booking> owner1All = bookingRepository.findByItem_Owner_Id(owner1.getId(), org.springframework.data.domain.Sort.by("start").descending());
        assertNotNull(owner1All);
        assertEquals(3, owner1All.size());

        List<Booking> owner1Past = bookingRepository.findByItem_Owner_IdAndEndIsBefore(owner1.getId(), LocalDateTime.now(), org.springframework.data.domain.Sort.by("start").descending());
        assertNotNull(owner1Past);
        assertTrue(owner1Past.stream().allMatch(b -> b.getEnd().isBefore(LocalDateTime.now())));

        List<Booking> owner1Future = bookingRepository.findByItem_Owner_IdAndStartIsAfter(owner1.getId(), LocalDateTime.now(), org.springframework.data.domain.Sort.by("start").descending());
        assertNotNull(owner1Future);
        assertTrue(owner1Future.stream().allMatch(b -> b.getStart().isAfter(LocalDateTime.now())));

        List<Booking> owner2All = bookingRepository.findByItem_Owner_Id(owner2.getId(), org.springframework.data.domain.Sort.by("start").descending());
        assertNotNull(owner2All);
        assertEquals(3, owner2All.size());
    }

    @Test
    void findFirstBookings_forItem() {
        Optional<Booking> lastForItem1 = bookingRepository.findFirstByItem_IdAndStatusAndStartLessThanOrderByEndDesc(item1.getId(), BookingStatus.APPROVED, LocalDateTime.now());
        assertTrue(lastForItem1.isPresent());

        Optional<Booking> nextForItem2 = bookingRepository.findFirstByItem_IdAndStatusAndStartGreaterThanOrderByStartAsc(item2.getId(), BookingStatus.APPROVED, LocalDateTime.now());
        assertTrue(nextForItem2.isPresent());
        assertEquals(approvedBooking.getId(), nextForItem2.get().getId());
    }

    @Test
    void findByBooker_Item_EndBefore_and_byItemList_status_startComparisons() {
        List<Booking> bList = bookingRepository.findByBooker_IdAndItem_IdAndEndIsBefore(booker1.getId(), item1.getId(), LocalDateTime.now());
        assertNotNull(bList);

        assertTrue(bList.stream().anyMatch(b -> b.getId().equals(pastBooking.getId())));

        List<Long> ids = List.of(item1.getId(), item2.getId());
        List<Booking> startedBefore = bookingRepository.findByItem_IdInAndStatusAndStartLessThan(ids, BookingStatus.APPROVED, LocalDateTime.now());
        assertNotNull(startedBefore);
        assertTrue(startedBefore.size() >= 1);

        List<Booking> startAfter = bookingRepository.findByItem_IdInAndStatusAndStartGreaterThan(ids, BookingStatus.APPROVED, LocalDateTime.now());
        assertNotNull(startAfter);
        assertTrue(startAfter.stream().anyMatch(b -> b.getId().equals(futureBooking.getId()) || b.getId().equals(approvedBooking.getId())));
    }
}