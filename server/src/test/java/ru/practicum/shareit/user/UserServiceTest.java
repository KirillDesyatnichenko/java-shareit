package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserInputDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final UserService userService;

    private UserDto saved1;
    private UserDto saved2;
    private UserDto saved3;

    @BeforeEach
    void setUp() {

        UserInputDto u1 = new UserInputDto();
        u1.setName("user1");
        u1.setEmail("user1@mail.ru");
        saved1 = userService.createUser(u1);

        UserInputDto u2 = new UserInputDto();
        u2.setName("user2");
        u2.setEmail("user2@mail.ru");
        saved2 = userService.createUser(u2);

        UserInputDto u3 = new UserInputDto();
        u3.setName("user3");
        u3.setEmail("user3@mail.ru");
        saved3 = userService.createUser(u3);
    }

    @Test
    void addUserTest() {
        UserInputDto inputDto = new UserInputDto();
        inputDto.setName("Nina");
        inputDto.setEmail("nina_konina@mail.ru");

        UserDto userDto = userService.createUser(inputDto);

        assertNotNull(userDto);
        assertNotNull(userDto.getId());
        assertEquals("Nina", userDto.getName());
        assertEquals("nina_konina@mail.ru", userDto.getEmail());
    }

    @Test
    void addUserWithDuplicateEmail_shouldThrowValidationException() {

        UserInputDto dup = new UserInputDto();
        dup.setName("Anton");
        dup.setEmail(saved1.getEmail());

        ValidationException e = assertThrows(ValidationException.class,
                () -> userService.createUser(dup)
        );

        assertTrue(e.getMessage().contains("Пользователь с таким email уже существует"));
    }

    @Test
    void updateUser_fullUpdate() {
        UserInputDto update = new UserInputDto();
        update.setName("Anton");
        update.setEmail("Anton@mail.ru");

        UserDto updated = userService.updateUser(saved3.getId(), update);

        assertNotNull(updated);
        assertEquals(saved3.getId(), updated.getId());
        assertEquals("Anton", updated.getName());
        assertEquals("Anton@mail.ru", updated.getEmail());
    }

    @Test
    void updateUser_partialNameOnly() {
        UserInputDto update = new UserInputDto();
        update.setName("new_user_name");
        UserDto updated = userService.updateUser(saved1.getId(), update);

        assertNotNull(updated);
        assertEquals(saved1.getId(), updated.getId());
        assertEquals("new_user_name", updated.getName());
        assertEquals(saved1.getEmail(), updated.getEmail());
    }

    @Test
    void updateUser_partialEmailOnly() {
        UserInputDto update = new UserInputDto();
        update.setEmail("new_user_email@mail.ru");
        UserDto updated = userService.updateUser(saved1.getId(), update);

        assertNotNull(updated);
        assertEquals(saved1.getId(), updated.getId());
        assertEquals("user1", updated.getName());
        assertEquals("new_user_email@mail.ru", updated.getEmail());
    }

    @Test
    void updateUser_withWrongUserId_shouldThrowNotFound() {
        UserInputDto update = new UserInputDto();
        update.setName("Anton");
        update.setEmail("Anton@mail.ru");

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.updateUser(9999L, update)
        );

        assertTrue(e.getMessage().toLowerCase().contains("не найден") || e.getMessage().toLowerCase().contains("not found"));
    }

    @Test
    void getUserByIdTest() {
        UserDto dto = userService.getUserById(saved2.getId());

        assertNotNull(dto);
        assertEquals(saved2.getName(), dto.getName());
        assertEquals(saved2.getEmail(), dto.getEmail());
    }

    @Test
    void getUserByWrongId_shouldThrowNotFound() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.getUserById(9999L)
        );

        assertTrue(e.getMessage().toLowerCase().contains("не найден") || e.getMessage().toLowerCase().contains("not found"));
    }

    @Test
    void getAllUsersListTest() {
        List<UserDto> userDtoList = userService.getAllUsers();

        assertNotNull(userDtoList);
        assertTrue(userDtoList.size() >= 3);
        assertTrue(userDtoList.stream().anyMatch(u -> u.getId().equals(saved1.getId())));
    }

    @Test
    void deleteUserById_flow() {
        userService.deleteUser(saved1.getId());

        assertThrows(NotFoundException.class, () -> userService.getUserById(saved1.getId()));
    }

    @Test
    void updateUser_duplicateEmail_shouldThrowValidationException() {
        UserInputDto update = new UserInputDto();
        update.setEmail(saved2.getEmail());

        ValidationException ex = assertThrows(ValidationException.class,
                () -> userService.updateUser(saved1.getId(), update));
        assertTrue(ex.getMessage().contains("Email уже используется другим пользователем"));
    }

    @Test
    void updateUser_emptyNameOrEmail_shouldNotChangeFields() {
        UserInputDto update = new UserInputDto();
        update.setName("");
        update.setEmail("   ");

        UserDto updated = userService.updateUser(saved1.getId(), update);
        assertEquals(saved1.getName(), updated.getName());
        assertEquals(saved1.getEmail(), updated.getEmail());
    }
}