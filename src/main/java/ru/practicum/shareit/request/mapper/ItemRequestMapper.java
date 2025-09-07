package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRrequest) {
        if (itemRrequest == null) {
            return null;
        }
        return ItemRequestDto.builder()
                .id(itemRrequest.getId())
                .description(itemRrequest.getDescription())
                .requestorId(itemRrequest.getRequestor() != null ? itemRrequest.getRequestor().getId() : null)
                .created(itemRrequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestInputDto requestInput, User requestor) {
        if (requestInput == null) {
            return null;
        }
        return ItemRequest.builder()
                .description(requestInput.getDescription())
                .requestor(requestor)
                .created(requestInput.getCreated())
                .build();
    }
}