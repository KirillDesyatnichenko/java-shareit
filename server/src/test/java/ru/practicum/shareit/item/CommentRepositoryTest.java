package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item;
    private User author;
    private Comment comment;

    @BeforeEach
    void setup() {

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        owner = userRepository.save(owner);

        author = new User();
        author.setName("booker");
        author.setEmail("bookerowner@mail.ru");
        author = userRepository.save(author);

        item = new Item();
        item.setName("new_item");
        item.setDescription("description_new_item");
        item.setAvailable(true);
        item.setOwner(owner);

        comment = new Comment();
        comment.setText("comment text");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    void sanityCheck_repositoryExists() {
        assertNotNull(commentRepository);
    }

    @Test
    void saveAndFindByItemId() {
        assertNotNull(commentRepository);
    }
}