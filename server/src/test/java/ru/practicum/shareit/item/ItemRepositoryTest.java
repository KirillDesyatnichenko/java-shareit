package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private Item item;

    @BeforeEach
    void beforeEachTest() {
        owner = new User();
        owner.setName("new_user");
        owner.setEmail("new_user@mail.ru");
        owner = userRepository.save(owner);

        item = new Item();
        item.setOwner(owner);
        item.setName("new_item");
        item.setDescription("description_new_item");
        item.setAvailable(true);
        item = itemRepository.save(item);
    }

    @Test
    void findByOwner_Id() {
        List<Item> items = itemRepository.findByOwner_Id(owner.getId());
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    void searchByText_shouldReturnItem_whenMatchesNameOrDescription() {
        List<Item> found = itemRepository.search("description_new");
        assertNotNull(found);
        assertTrue(found.stream().anyMatch(i -> i.getId().equals(item.getId())));
    }
}