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
import ru.practicum.shareit.exeption.ForbiddenException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
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

    @Test
    void updateItem_notOwner_shouldThrowForbidden() {
        User stranger = new User();
        stranger.setName("Stranger");
        stranger.setEmail("stranger@mail.ru");
        stranger = userRepository.save(stranger);

        ItemInputDto input = new ItemInputDto();
        input.setName("Чужая вещь");

        User finalStranger = stranger;
        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> itemService.updateItem(finalStranger.getId(), item.getId(), input));
        assertEquals("Пользователь " + stranger.getId() + " не может обновить чужую вещь.", ex.getMessage());
    }

    @Test
    void addComment_noPastBooking_shouldThrowValidation() {
        CommentDto dto = new CommentDto();
        dto.setText("Невозможно комментировать");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.addComment(owner.getId(), item.getId(), dto));
        assertEquals("Пользователь не может оставить комментарий без завершённого бронирования вещи", ex.getMessage());
    }

    @Test
    void createItem_withNonExistentUser_shouldThrowException() {
        ItemInputDto input = new ItemInputDto();
        input.setName("Новая вещь");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemService.createItem(999L, input));
        assertTrue(ex.getMessage().contains("Пользователь с id 999 не найден"));
    }

    @Test
    void createItem_withNonExistentRequest_shouldThrowException() {
        ItemInputDto input = new ItemInputDto();
        input.setName("Вещь");
        input.setRequestId(999L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemService.createItem(owner.getId(), input));
        assertTrue(ex.getMessage().contains("Запрос с id 999 не найден"));
    }

    @Test
    void getItemById_nonExistentItem_shouldThrowException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemService.getItemById(999L));
        assertTrue(ex.getMessage().contains("Вещь с id 999 не найдена"));
    }

    @Test
    void createItem_withoutRequest_shouldSucceed() {
        ItemInputDto input = new ItemInputDto();
        input.setName("Без запроса");
        input.setDescription("Просто вещь");
        input.setAvailable(true);

        ItemDto created = itemService.createItem(owner.getId(), input);
        assertNotNull(created);
        assertEquals("Без запроса", created.getName());
    }

    @Test
    void updateItem_allFields() {
        ItemInputDto input = new ItemInputDto();
        input.setName("Новое имя");
        input.setDescription("Новое описание");
        input.setAvailable(false);

        ItemDto updated = itemService.updateItem(owner.getId(), item.getId(), input);
        assertEquals("Новое имя", updated.getName());
        assertEquals("Новое описание", updated.getDescription());
        assertFalse(updated.getAvailable());
    }

    @Test
    void getUserItems_emptyAndWithNullBookingsComments() {
        User newUser = new User();
        newUser.setName("Empty");
        newUser.setEmail("empty@mail.ru");
        newUser = userRepository.save(newUser);

        List<ItemDto> emptyItems = itemService.getUserItems(newUser.getId());
        assertTrue(emptyItems.isEmpty());

        Item newItem = new Item();
        newItem.setName("Тестовая вещь");
        newItem.setDescription("Нет бронирований");
        newItem.setAvailable(true);
        newItem.setOwner(owner);
        newItem = itemRepository.save(newItem);

        Item savedItem = itemRepository.save(newItem);

        List<ItemDto> items = itemService.getUserItems(owner.getId());
        assertTrue(items.stream().anyMatch(i -> i.getId().equals(savedItem.getId())));
    }

    @Test
    void itemMapper_nullInputs_shouldReturnNull() {
        assertNull(ItemMapper.toItem(null, null));
        assertNull(ItemMapper.toItemDto(null));
        assertNull(ItemMapper.toItemWithBookingDto(null, null, null));
    }

    @Test
    void commentMapper_nullInputs_shouldReturnNull() {
        assertNull(CommentMapper.toEntity(null, null, null));
        assertNull(CommentMapper.toDto(null));
    }
}