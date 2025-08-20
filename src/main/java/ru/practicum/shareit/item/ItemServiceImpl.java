package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.common.BaseService;
import ru.practicum.shareit.exeption.ForbiddenException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends BaseService implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemInputDto inputDto) {
        User owner = getOrThrow(userRepository.findById(userId),
                "Пользователь с id " + userId + " не найден");

        Item item = ItemMapper.toItem(inputDto, null);
        item.setOwner(owner);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemInputDto inputDto) {
        Item existing = getOrThrow(itemRepository.findById(itemId),
                "Вещь не найдена: " + itemId);

        if (!existing.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь " + userId + " не может обновить чужую вещь.");
        }

        if (inputDto.getName() != null && !inputDto.getName().isBlank()) {
            existing.setName(inputDto.getName());
        }
        if (inputDto.getDescription() != null && !inputDto.getDescription().isBlank()) {
            existing.setDescription(inputDto.getDescription());
        }
        if (inputDto.getAvailable() != null) {
            existing.setAvailable(inputDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.update(existing));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        getOrThrow(userRepository.findById(userId),
                "Пользователь с id " + userId + " не найден");

        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}