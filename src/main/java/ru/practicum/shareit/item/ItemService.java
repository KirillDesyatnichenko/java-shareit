package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInputDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemInputDto inputDto);
    ItemDto updateItem(Long userId, Long itemId, ItemInputDto inputDto);
    ItemDto getItemById(Long itemId);
    List<ItemDto> getUserItems(Long userId);
    List<ItemDto> searchItems(String text);
}