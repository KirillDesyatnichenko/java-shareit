package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody ItemInputDto inputDto) {
        log.info("Пользователь с id={} добавил новую вещь", userId);
        return ResponseEntity.ok(itemService.createItem(userId, inputDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemInputDto inputDto) {
        log.info("Пользователь с id={} обновил характеристики вещи с id={}", userId, itemId);
        return ResponseEntity.ok(itemService.updateItem(userId, itemId, inputDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId) {
        log.info("Запрошена информация о вещи с id={}", itemId);
        return ResponseEntity.ok(itemService.getItemById(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрошена информация о всех вещах Пользователя с id={}", userId);
        return ResponseEntity.ok(itemService.getUserItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        log.info("Произведен поиск вещи");
        return ResponseEntity.ok(itemService.searchItems(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Пользователь с id={} оставил комментарий к вещи с id={}", userId, itemId);
        return ResponseEntity.ok(itemService.addComment(userId, itemId, commentDto));
    }
}