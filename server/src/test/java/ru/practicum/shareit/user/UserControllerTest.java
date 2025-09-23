package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserInputDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;

    @Test
    void getAllUsersList() throws Exception {
        UserDto user = UserDto.builder()
                .id(userId)
                .name("Kolya")
                .email("Kolya_loh@mail.ru")
                .build();

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userId))
                .andExpect(jsonPath("$[0].name").value("Kolya"))
                .andExpect(jsonPath("$[0].email").value("Kolya_loh@mail.ru"));
    }

    @Test
    void getUserById() throws Exception {
        UserDto user = UserDto.builder()
                .id(userId)
                .name("Ivan")
                .email("Vano@mail.ru")
                .build();

        Mockito.when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("Vano@mail.ru"));
    }

    @Test
    void createUser() throws Exception {
        UserInputDto inputDto = new UserInputDto();
        inputDto.setName("Nina");
        inputDto.setEmail("Nina_konina@mail.ru");

        UserDto responseDto = UserDto.builder()
                .id(userId)
                .name("Nina")
                .email("Nina_konina@mail.ru")
                .build();

        Mockito.when(userService.createUser(any())).thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Nina"))
                .andExpect(jsonPath("$.email").value("Nina_konina@mail.ru"));
    }

    @Test
    void updateUser() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Update");
        updateDto.setEmail("update@mail.ru");

        UserDto updatedUser = UserDto.builder()
                .id(userId)
                .name("Update")
                .email("update@mail.ru")
                .build();

        Mockito.when(userService.updateUser(eq(userId), any())).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Update"))
                .andExpect(jsonPath("$.email").value("update@mail.ru"));
    }

    @Test
    void deleteUserById() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
    }
}