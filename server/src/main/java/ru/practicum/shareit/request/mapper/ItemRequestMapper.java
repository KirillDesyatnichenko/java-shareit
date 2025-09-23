package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest req, List<ItemShortDto> items) {
        if (req == null) return null;
        return ItemRequestDto.builder()
                .id(req.getId())
                .description(req.getDescription())
                .requestorId(req.getRequestor() != null ? req.getRequestor().getId() : null)
                .created(req.getCreated())
                .items(items == null ? List.of() : items)
                .build();
    }

    public static ItemRequest toEntity(ItemRequestInputDto inputDto, User requestor) {
        if (inputDto == null) return null;
        return ItemRequest.builder()
                .description(inputDto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }
}