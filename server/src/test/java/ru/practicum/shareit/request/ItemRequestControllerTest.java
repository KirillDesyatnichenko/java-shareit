package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private final Long requestId = 2L;

    @Test
    void postRequest_shouldReturnDto() throws Exception {
        ItemRequestInputDto input = new ItemRequestInputDto();
        input.setDescription("Нужна шапочка из фольги!");

        ItemRequestDto out = ItemRequestDto.builder()
                .id(requestId)
                .description("Нужна шапочка из фольги!")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        Mockito.when(itemRequestService.createRequest(eq(userId), any(ItemRequestInputDto.class)))
                .thenReturn(out);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Нужна шапочка из фольги!"));
    }

    @Test
    void getOwnRequests_shouldReturnList() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(requestId)
                .description("Нужен бюст Ленина")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        Mockito.when(itemRequestService.getOwnRequests(eq(userId)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].description").value("Нужен бюст Ленина"));
    }

    @Test
    void getAllRequests_withPaging_shouldReturnList() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(requestId)
                .description("Нужен бюст Сталина")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        Mockito.when(itemRequestService.getAllRequests(eq(userId), anyInt(), anyInt()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].description").value("Нужен бюст Сталина"));
    }

    @Test
    void getRequestById_shouldReturnDto() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(requestId)
                .description("Нужен бюст Берии")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        Mockito.when(itemRequestService.getRequestById(eq(userId), eq(requestId)))
                .thenReturn(dto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Нужен бюст Берии"));
    }
}