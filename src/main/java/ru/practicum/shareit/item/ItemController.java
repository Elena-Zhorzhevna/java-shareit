package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    public List<ItemDto> getAllItemsFromUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
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
    public Optional<ItemDto> getItemById(@PathVariable("itemId") long itemId) {
        log.info("Запрос на получение вещи id=" + itemId);
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
    public ItemDto create(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                          @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос на добавление вещи пользователя id = " + userId);
        return itemService.addItem(userId, itemDto);
    }

    /**
     * Обрабатывает PATCH-запрос на обновление вещи.
     *
     * @param itemId  Идентификатор вещи.
     * @param newItem Обновленная вещь.
     * @return Обновленная вещь в формате Dto
     */
    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody Item newItem) {
        log.info("Запрос на обновление вещи id = " + itemId + " от пользователя id = " + userId);
        return itemService.updateItem(userId, itemId, newItem);
    }

    /**
     * Удаляет вещь по ее идентификатору.
     */
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeItemById(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                               @PathVariable Long itemId) {
        log.info("Запрос на удаление вещи владельца c id: {}, id вещи: {}.", userId, itemId);
        itemService.removeItemById(userId, itemId);
    }

    /**
     * Удаление всех вещей пользователя с указанным идентификатором.
     */
    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void removeAllItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на удаление всех вещей пользователя c id: {}", userId);
        itemService.removeAllItemsByOwnerId(userId);
    }
}