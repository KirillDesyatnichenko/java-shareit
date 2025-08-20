package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestInputDto {

    private String description;
    private Long requestorId;
    private LocalDateTime created;
}
