package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.BaseService;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl extends BaseService implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestInputDto createDto) {
        User requestor = getOrThrow(userRepository.findById(userId),
                "Пользователь с id " + userId + " не найден");

        ItemRequest entity = ItemRequestMapper.toEntity(createDto, requestor);
        ItemRequest saved = requestRepository.save(entity);
        return ItemRequestMapper.toDto(saved, List.of());
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {
        getOrThrow(userRepository.findById(userId),
                "Пользователь с id " + userId + " не найден");

        List<ItemRequest> requests = requestRepository.findByRequestor_IdOrderByCreatedDesc(userId);
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findByRequest_IdIn(requestIds);

        Map<Long, List<ItemShortDto>> itemsByRequest = items.stream()
                .collect(Collectors.groupingBy(
                        it -> it.getRequest() != null ? it.getRequest().getId() : null,
                        Collectors.mapping(ItemMapper::toShortDto, Collectors.toList())
                ));

        return requests.stream()
                .map(req -> ItemRequestMapper.toDto(req, itemsByRequest.getOrDefault(req.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        getOrThrow(userRepository.findById(userId),
                "Пользователь с id " + userId + " не найден");

        if (from < 0 || size <= 0) {
            throw new ValidationException("Неверные параметры пагинации");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        Page<ItemRequest> page = requestRepository.findByRequestor_IdNot(userId, pageable);
        List<ItemRequest> requests = page.getContent();

        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findByRequest_IdIn(requestIds);
        Map<Long, List<ItemShortDto>> itemsByRequest = items.stream()
                .collect(Collectors.groupingBy(
                        it -> it.getRequest() != null ? it.getRequest().getId() : null,
                        Collectors.mapping(ItemMapper::toShortDto, Collectors.toList())
                ));

        return requests.stream()
                .map(req -> ItemRequestMapper.toDto(req, itemsByRequest.getOrDefault(req.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        getOrThrow(userRepository.findById(userId),
                "Пользователь с id " + userId + " не найден");

        ItemRequest req = getOrThrow(requestRepository.findById(requestId),
                "Пользователь с id " + userId + " не найден");

        List<ItemShortDto> items = itemRepository.findByRequest_Id(req.getId()).stream()
                .map(ItemMapper::toShortDto)
                .collect(Collectors.toList());

        return ItemRequestMapper.toDto(req, items);
    }
}