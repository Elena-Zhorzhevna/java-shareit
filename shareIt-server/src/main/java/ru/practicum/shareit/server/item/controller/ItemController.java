package ru.practicum.shareit.server.item.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.ItemService;

import java.util.Collection;
import java.util.List;

/**
 * Класс контроллера для управления объектами Item в приложении ShareIt.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";

    @Qualifier("itemServiceImpl")
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Получение списка вещей определенного пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return Список вещей пользователя с указанным идентификатором.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllItemsFromUser(@RequestHeader(USER_ID_REQUEST_HEADER) Long userId) {
        log.info("Получение списка всех вещей владельца с id: {}", userId);
        return itemService.getAllItemsByUserId(userId);
    }

    /**
     * Обрабатывает GET-запрос на получение вещи по идентификатору.
     *
     * @param itemId Идентификатор вещи.
     * @return Вещь с указанным идентификатором.
     */
    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItemById(@PathVariable("itemId") Long itemId) {
        log.info("Запрос на получение вещи id = " + itemId);
        return itemService.getItemById(itemId);
    }

    /**
     * Поиск вещей.
     *
     * @param text Текст для поиска.
     * @return Искомая вещь.
     */
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> searchItemsByText(@RequestParam String text) {
        log.info("Запрос на поиск вещи. Текст запроса: " + text);
        return itemService.searchItemsByText(text);
    }

    /**
     * Обрабатывает POST-запрос на добавление вещи.
     *
     * @param itemDto Вещь, которую нужно добавить.
     * @return Добавленная вещь.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(USER_ID_REQUEST_HEADER) Long userId,
                          @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос на добавление вещи пользователя id = " + userId);
        return itemService.addItem(userId, itemDto);
    }

    /**
     * Обрабатывает PATCH-запрос на обновление вещи.
     *
     * @param itemId     Идентификатор вещи.
     * @param newItemDto Обновленная вещь.
     * @return Обновленная вещь в формате Dto
     */
    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader(USER_ID_REQUEST_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto newItemDto) {
        log.info("Запрос на обновление вещи id = " + itemId + " от пользователя id = " + userId);
        return itemService.updateItem(userId, itemId, newItemDto);
    }

    /**
     * Удаление вещи по ее идентификатору.
     */
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeItemById(@RequestHeader(USER_ID_REQUEST_HEADER) Long userId,
                               @PathVariable Long itemId) {
        log.info("Запрос на удаление вещи владельца c id: {}, id вещи: {}.", userId, itemId);
        itemService.removeItemById(userId, itemId);
    }

    /**
     * Удаление всех вещей пользователя с указанным идентификатором.
     */
    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void removeAllItemsByUser(@RequestHeader(USER_ID_REQUEST_HEADER) long userId) {
        log.info("Запрос на удаление всех вещей пользователя c id: {}", userId);
        itemService.removeAllItemsByOwnerId(userId);
    }

    /**
     * Добавление отзыва пользователем с указанным идентификатором для вещи с указанным идентификатором.
     *
     * @param commentDto Отзыв в формате ДТО.
     * @param userId     Идентификатор пользователя.
     * @param itemId     Идентификатор вещи.
     * @return Отзыв пользователя.
     */
    @ResponseBody
    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader(USER_ID_REQUEST_HEADER) Long userId) {
        log.info("Получен запрос на добавление комментария пользователем с id = {} для вещи с id = {}",
                userId, itemId);
        return itemService.createComment(commentDto, itemId, userId);
    }
}