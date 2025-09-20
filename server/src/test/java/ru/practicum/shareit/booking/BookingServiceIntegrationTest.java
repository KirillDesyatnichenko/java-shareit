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
import ru.practicum.shareit.exeption.ForbiddenException;
import ru.practicum.shareit.exeption.ValidationException;
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

    @Test
    void createBooking_ownerBookingOwnItem_shouldThrowForbidden() {
        BookingInputDto input = new BookingInputDto();
        input.setItemId(item.getId());
        input.setStart(LocalDateTime.now().plusDays(1));
        input.setEnd(LocalDateTime.now().plusDays(2));

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> bookingService.createBooking(owner.getId(), input));
        assertEquals("Владелец не может бронировать свою вещь", ex.getMessage());
    }

    @Test
    void getBooking_userNotOwnerOrBooker_shouldThrowForbidden() {
        User stranger = new User();
        stranger.setName("Anton");
        stranger.setEmail("Anton@mail.ru");
        stranger = userRepository.save(stranger);

        BookingInputDto input = new BookingInputDto();
        input.setItemId(item.getId());
        input.setStart(LocalDateTime.now().plusDays(1));
        input.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto booking = bookingService.createBooking(booker.getId(), input);

        User finalStranger = stranger;
        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> bookingService.getBooking(finalStranger.getId(), booking.getId()));
        assertTrue(ex.getMessage().contains("Нет доступа к бронированию"));
    }

    @Test
    void approveBooking_notOwner_shouldThrowForbidden() {
        BookingInputDto input = new BookingInputDto();
        input.setItemId(item.getId());
        input.setStart(LocalDateTime.now().plusDays(1));
        input.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto booking = bookingService.createBooking(booker.getId(), input);

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> bookingService.approveBooking(booker.getId(), booking.getId(), true));
        assertEquals("Только владелец вещи может изменять статус бронирования", ex.getMessage());
    }

    @Test
    void approveBooking_alreadyApproved_shouldThrowValidation() {
        BookingInputDto input = new BookingInputDto();
        input.setItemId(item.getId());
        input.setStart(LocalDateTime.now().plusDays(1));
        input.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto booking = bookingService.createBooking(booker.getId(), input);
        bookingService.approveBooking(owner.getId(), booking.getId(), true);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(owner.getId(), booking.getId(), true));
        assertTrue(ex.getMessage().contains("Статус уже был установлен"));
    }

    @Test
    void createBooking_invalidDates_shouldThrowValidation() {
        BookingInputDto input = new BookingInputDto();
        input.setItemId(item.getId());
        input.setStart(LocalDateTime.now().plusDays(2));
        input.setEnd(LocalDateTime.now().plusDays(1));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booker.getId(), input));
        assertEquals("Дата окончания должна быть позже даты начала", ex.getMessage());
    }

    @Test
    void getBookingsForUser_allStates() {
        BookingInputDto input = new BookingInputDto();
        input.setItemId(item.getId());
        input.setStart(LocalDateTime.now().plusDays(1));
        input.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto booking = bookingService.createBooking(booker.getId(), input);

        bookingService.getBookingsForUser(booker.getId(), BookingState.CURRENT);
        bookingService.getBookingsForUser(booker.getId(), BookingState.PAST);
        bookingService.getBookingsForUser(booker.getId(), BookingState.FUTURE);
        bookingService.getBookingsForUser(booker.getId(), BookingState.WAITING);
        bookingService.getBookingsForUser(booker.getId(), BookingState.REJECTED);
    }

    @Test
    void getBookingsForOwner_allStates() {
        BookingInputDto input = new BookingInputDto();
        input.setItemId(item.getId());
        input.setStart(LocalDateTime.now().plusDays(1));
        input.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto booking = bookingService.createBooking(booker.getId(), input);

        bookingService.getBookingsForOwner(owner.getId(), BookingState.CURRENT);
        bookingService.getBookingsForOwner(owner.getId(), BookingState.PAST);
        bookingService.getBookingsForOwner(owner.getId(), BookingState.FUTURE);
        bookingService.getBookingsForOwner(owner.getId(), BookingState.WAITING);
        bookingService.getBookingsForOwner(owner.getId(), BookingState.REJECTED);
    }
}