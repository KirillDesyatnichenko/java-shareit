package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ru.practicum.shareit.ShareItServer.class)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void init() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.ru");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@mail.ru");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Носовой кавыряк");
        item.setDescription("Можно засунуть сразу три пальца");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void createBooking_and_states_flow() {
        BookingInputDto input = new BookingInputDto();
        input.setItemId(item.getId());
        input.setStart(LocalDateTime.now().plusDays(1));
        input.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.createBooking(booker.getId(), input);
        assertNotNull(created);
        assertEquals(BookingStatus.WAITING, created.getStatus());

        BookingDto approved = bookingService.approveBooking(owner.getId(), created.getId(), true);
        assertNotNull(approved);
        assertEquals(BookingStatus.APPROVED, approved.getStatus());

        BookingDto byBooker = bookingService.getBooking(booker.getId(), created.getId());
        assertNotNull(byBooker);

        List<BookingDto> allForBooker = bookingService.getBookingsForUser(booker.getId(), BookingState.ALL);
        assertNotNull(allForBooker);
        assertTrue(allForBooker.stream().anyMatch(b -> b.getId().equals(created.getId())));

        List<BookingDto> allForOwner = bookingService.getBookingsForOwner(owner.getId(), BookingState.ALL);
        assertNotNull(allForOwner);
        assertTrue(allForOwner.stream().anyMatch(b -> b.getId().equals(created.getId())));
    }

    @Test
    void createBooking_itemNotAvailable_shouldThrow() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingInputDto input = new BookingInputDto();
        input.setItemId(item.getId());
        input.setStart(LocalDateTime.now().plusDays(1));
        input.setEnd(LocalDateTime.now().plusDays(2));

        Exception ex = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(booker.getId(), input));
        assertNotNull(ex.getMessage());
    }

    @Test
    void getLastAndNextBookingForItem() {
        BookingInputDto past = new BookingInputDto();
        past.setItemId(item.getId());
        past.setStart(LocalDateTime.now().minusDays(5));
        past.setEnd(LocalDateTime.now().minusDays(4));
        BookingDto bPast = bookingService.createBooking(booker.getId(), past);
        bookingService.approveBooking(owner.getId(), bPast.getId(), true);

        BookingInputDto next = new BookingInputDto();
        next.setItemId(item.getId());
        next.setStart(LocalDateTime.now().plusDays(4));
        next.setEnd(LocalDateTime.now().plusDays(5));
        BookingDto bNext = bookingService.createBooking(booker.getId(), next);
        bookingService.approveBooking(owner.getId(), bNext.getId(), true);

        assertTrue(bookingService.getLastBookingForItem(item.getId()).isPresent());
        assertTrue(bookingService.getNextBookingForItem(item.getId()).isPresent());
    }
}