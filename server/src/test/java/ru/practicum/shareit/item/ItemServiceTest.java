package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ru.practicum.shareit.ShareItServer.class)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

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
        item.setName("Шняжка");
        item.setDescription("Волшебная");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void createUpdateGetAndSearchFlow() {
        ItemInputDto input = new ItemInputDto();
        input.setName("Молот тора");
        input.setDescription("Лёгкий");
        input.setAvailable(true);

        ItemDto created = itemService.createItem(owner.getId(), input);
        assertNotNull(created);
        assertEquals("Молот тора", created.getName());

        ItemInputDto update = new ItemInputDto();
        update.setName("Молот не тора");
        ItemDto updated = itemService.updateItem(owner.getId(), created.getId(), update);
        assertEquals("Молот не тора", updated.getName());

        ItemDto byId = itemService.getItemById(created.getId());
        assertNotNull(byId);

        List<ItemDto> found = itemService.searchItems("Молот не тора");
        assertTrue(found.stream().anyMatch(i -> i.getId().equals(created.getId())));
    }

    @Test
    void addComment_allowedOnlyAfterPastApprovedBooking() {
        Booking past = Booking.builder()
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(past);

        CommentDto dto = new CommentDto();
        dto.setText("Ыыы!");

        CommentDto saved = itemService.addComment(booker.getId(), item.getId(), dto);

        assertNotNull(saved);
        assertEquals("Ыыы!", saved.getText());
        assertNotNull(saved.getAuthorName());
    }

    @Test
    void addComment_forbiddenWithoutPastBooking() {
        CommentDto dto = new CommentDto();
        dto.setText("Кака");

        Exception ex = assertThrows(RuntimeException.class,
                () -> itemService.addComment(booker.getId(), item.getId(), dto));
        assertNotNull(ex.getMessage());
    }
}