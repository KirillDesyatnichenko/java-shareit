package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.BaseService;
import ru.practicum.shareit.exeption.ForbiddenException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends BaseService implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

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

        return ItemMapper.toItemDto(itemRepository.save(existing));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));

        List<CommentDto> comments = commentRepository.findByItem_Id(itemId).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        ItemDto dto = ItemMapper.toItemDto(item);
        dto.setComments(comments);
        return dto;
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        getOrThrow(userRepository.findById(userId),
                "Пользователь с id " + userId + " не найден");

        List<Item> items = itemRepository.findByOwner_Id(userId);
        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        List<Comment> comments = commentRepository.findByItem_IdIn(itemIds);
        Map<Long, List<CommentDto>> commentsByItem = comments.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getItem().getId(),
                        Collectors.mapping(CommentMapper::toDto, Collectors.toList())
                ));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> lastBookings = bookingRepository.findByItem_IdInAndStatusAndStartLessThan(
                itemIds, BookingStatus.APPROVED, now);
        Map<Long, BookingShortDto> lastBookingMap = lastBookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getItem().getId(),
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Booking::getEnd)),
                                opt -> opt.map(BookingMapper::toShortDto).orElse(null)
                        )
                ));

        List<Booking> nextBookings = bookingRepository.findByItem_IdInAndStatusAndStartGreaterThan(
                itemIds, BookingStatus.APPROVED, now);
        Map<Long, BookingShortDto> nextBookingMap = nextBookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getItem().getId(),
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparing(Booking::getStart)),
                                opt -> opt.map(BookingMapper::toShortDto).orElse(null)
                        )
                ));

        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemWithBookingDto(item,
                            lastBookingMap.get(item.getId()),
                            nextBookingMap.get(item.getId()));

                    dto.setComments(commentsByItem.getOrDefault(item.getId(), List.of()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text).stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemWithBookingDto(item, null, null);
                    dto.setComments(List.of());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        User author = getOrThrow(userRepository.findById(userId),
                "Пользователь с id " + userId + " не найден");

        Item item = getOrThrow(itemRepository.findById(itemId),
                "Вещь не найдена: " + itemId);

        boolean hasPastBooking = bookingRepository
                .findByBooker_IdAndItem_IdAndEndIsBefore(userId, itemId, LocalDateTime.now())
                .stream()
                .anyMatch(b -> b.getStatus() == BookingStatus.APPROVED);

        if (!hasPastBooking) {
            throw new ValidationException("Пользователь не может оставить комментарий без завершённого бронирования вещи");
        }

        Comment comment = Comment.builder()
                .text(dto.getText())
                .author(author)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        return CommentMapper.toDto(commentRepository.save(comment));
    }
}