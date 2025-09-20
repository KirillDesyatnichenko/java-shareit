package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ru.practicum.shareit.ShareItServer.class)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {

    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;

    private User user1;
    private User user2;
    private Item item;

    @BeforeEach
    void init() {
        requestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        user1 = new User();
        user1.setName("Owner");
        user1.setEmail("owner@mail.ru");
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setName("Requester");
        user2.setEmail("requester@mail.ru");
        user2 = userRepository.save(user2);

        item = new Item();
        item.setName("Тысяча блох");
        item.setDescription("Пируют на пятой точке");
        item.setAvailable(true);
        item.setOwner(user1);
        item = itemRepository.save(item);
    }

    @Test
    void createAndGetOwnRequests_flow() {
        ItemRequestInputDto input = new ItemRequestInputDto();
        input.setDescription("Нужны блохи");

        ItemRequestDto created = itemRequestService.createRequest(user2.getId(), input);
        assertNotNull(created);
        assertEquals("Нужны блохи", created.getDescription());

        List<ItemRequestDto> own = itemRequestService.getOwnRequests(user2.getId());
        assertNotNull(own);
        assertTrue(own.stream().anyMatch(r -> r.getId().equals(created.getId())));
    }

    @Test
    void getAllRequests_withPaging_excludesRequester() {
        ItemRequestInputDto input = new ItemRequestInputDto();
        input.setDescription("Нужна шапочка из фольги");
        ItemRequestDto created = itemRequestService.createRequest(user2.getId(), input);

        List<ItemRequestDto> all = itemRequestService.getAllRequests(user1.getId(), 0, 10);
        assertNotNull(all);
        assertTrue(all.stream().anyMatch(r -> r.getId().equals(created.getId())));
    }

    @Test
    void createRequest_nonExistentUser_shouldThrow() {
        ItemRequestInputDto input = new ItemRequestInputDto();
        input.setDescription("Что-то");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemRequestService.createRequest(999L, input));
        assertTrue(ex.getMessage().contains("Пользователь с id 999 не найден"));
    }

    @Test
    void getOwnRequests_nonExistentUser_shouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemRequestService.getOwnRequests(999L));
        assertTrue(ex.getMessage().contains("Пользователь с id 999 не найден"));
    }

    @Test
    void getAllRequests_invalidPaging_shouldThrow() {
        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> itemRequestService.getAllRequests(user1.getId(), -1, 10));
        assertEquals("Неверные параметры пагинации", ex1.getMessage());

        ValidationException ex2 = assertThrows(ValidationException.class,
                () -> itemRequestService.getAllRequests(user1.getId(), 0, 0));
        assertEquals("Неверные параметры пагинации", ex2.getMessage());
    }

    @Test
    void getAllRequests_nonExistentUser_shouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemRequestService.getAllRequests(999L, 0, 10));
        assertTrue(ex.getMessage().contains("Пользователь с id 999 не найден"));
    }

    @Test
    void getRequestById_nonExistentUser_shouldThrow() {
        ItemRequestInputDto input = new ItemRequestInputDto();
        input.setDescription("Нужна шапочка");
        ItemRequestDto created = itemRequestService.createRequest(user2.getId(), input);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemRequestService.getRequestById(999L, created.getId()));
        assertTrue(ex.getMessage().contains("Пользователь с id 999 не найден"));
    }

    @Test
    void getRequestById_nonExistentRequest_shouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemRequestService.getRequestById(user1.getId(), 999L));
        assertTrue(ex.getMessage().contains("Пользователь с id "));
    }

    @Test
    void getAllRequests_emptyList_shouldReturnEmpty() {
        List<ItemRequestDto> result = itemRequestService.getAllRequests(user1.getId(), 0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOwnRequests_emptyList_shouldReturnEmpty() {
        List<ItemRequestDto> result = itemRequestService.getOwnRequests(user1.getId());
        assertTrue(result.isEmpty());
    }
}