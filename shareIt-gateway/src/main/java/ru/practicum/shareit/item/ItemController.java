package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на создание вещи.");
        final ResponseEntity<Object> item = itemClient.createItem(ownerId, itemDto);
        log.info("Вещь создана.");
        return item;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Valid @RequestBody ItemDto newItemDto,
                                         @PathVariable(value = "itemId") Long itemId,
                                         @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на обновление вещи с id {}", itemId);
        final ResponseEntity<Object> item = itemClient.updateItem(itemId, ownerId, newItemDto);
        log.info("Обновлена вещь с id {}", itemId);
        return item;
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на получение всех вещей пользователя.");
        final ResponseEntity<Object> item = itemClient.getAllItemsByUserId(ownerId);
        log.info("Получены все вещи пользователя.");
        return item;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable(value = "itemId") Long itemId,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение вещи с id = {}", itemId);
        final ResponseEntity<Object> item = itemClient.getItemById(itemId, userId);
        log.info("Получена вещь с id = {}", itemId);
        return item;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getBySearch(@RequestParam(value = "text") String text,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение вещи, содержащей в названии или описании текст: {}", text);
        final ResponseEntity<Object> item = itemClient.getBySearch(userId, text);
        log.info("Завершен поиск вещи, содержащей текст: {}", text);
        return item;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @PathVariable(name = "itemId") Long itemId,
                                                @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавления отзыва для вещи с id = {}", itemId);
        final ResponseEntity<Object> comment = itemClient.createComment(itemId, userId, commentDto);
        log.info("Добавлен отзыв на вещь с id = {}", itemId);
        return comment;
    }
}