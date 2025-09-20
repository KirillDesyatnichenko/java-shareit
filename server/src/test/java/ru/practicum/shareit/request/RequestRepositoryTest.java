package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RequestRepositoryTest {

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    private User requestor;
    private ItemRequest request;

    @BeforeEach
    void beforeEachTest() {
        requestRepository.deleteAll();
        userRepository.deleteAll();

        requestor = new User();
        requestor.setName("new_user");
        requestor.setEmail("new_user@mail.ru");
        requestor = userRepository.save(requestor);

        request = ItemRequest.builder()
                .description("request_description")
                .requestor(requestor)
                .created(LocalDateTime.of(2025, 2, 7, 0, 0))
                .build();
        request = requestRepository.save(request);
    }

    @Test
    void findByRequestor_IdOrderByCreatedDesc_shouldReturnList() {
        List<ItemRequest> list = requestRepository.findByRequestor_IdOrderByCreatedDesc(requestor.getId());
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(request.getId(), list.get(0).getId());
    }

    @Test
    void findByRequestor_IdNot_withPaging_shouldReturnEmptyForOtherUser() {
        var page = requestRepository.findByRequestor_IdNot(requestor.getId(), org.springframework.data.domain.PageRequest.of(0, 10));
        assertNotNull(page);
        assertTrue(page.getContent().isEmpty());
    }
}
