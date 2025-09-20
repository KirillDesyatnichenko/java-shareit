package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createItem_shouldReturnItemDto() throws Exception {
        ItemInputDto input = new ItemInputDto();
        input.setName("Туалетная бумага");
        input.setDescription("б/у");
        input.setAvailable(true);

        ItemDto out = ItemDto.builder()
                .id(1L)
                .name("Туалетная бумага")
                .description("б/у")
                .available(true)
                .build();

        Mockito.when(itemService.createItem(anyLong(), any())).thenReturn(out);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Туалетная бумага"));
    }

    @Test
    void updateItem_shouldReturnUpdated() throws Exception {
        ItemInputDto input = new ItemInputDto();
        input.setName("Updated");
        ItemDto out = ItemDto.builder().id(2L).name("Updated").build();

        Mockito.when(itemService.updateItem(anyLong(), eq(2L), any())).thenReturn(out);

        mockMvc.perform(patch("/items/{itemId}", 2L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void getItem_shouldReturnItem() throws Exception {
        ItemDto out = ItemDto.builder().id(3L).name("Палка").build();
        Mockito.when(itemService.getItemById(3L)).thenReturn(out);

        mockMvc.perform(get("/items/{itemId}", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Палка"));
    }

    @Test
    void getUserItems_shouldReturnList() throws Exception {
        ItemDto out = ItemDto.builder().id(4L).name("Ишак").build();
        Mockito.when(itemService.getUserItems(1L)).thenReturn(List.of(out));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[0].name").value("Ишак"));
    }

    @Test
    void searchItems_shouldReturnList() throws Exception {
        ItemDto out = ItemDto.builder().id(5L).name("Ишак").build();
        Mockito.when(itemService.searchItems("ишак")).thenReturn(List.of(out));

        mockMvc.perform(get("/items/search").param("text", "ишак"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].name").value("Ишак"));
    }

    @Test
    void addComment_shouldReturnComment() throws Exception {
        CommentDto create = new CommentDto();
        create.setText("Что это за шняга?!");

        CommentDto out = CommentDto.builder().id(1L).text("Что это за шняга?!").authorName("Антон").build();

        Mockito.when(itemService.addComment(eq(1L), eq(10L), any())).thenReturn(out);

        mockMvc.perform(post("/items/{itemId}/comment", 10L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Что это за шняга?!"));
    }
}