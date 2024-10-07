package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;
import java.util.List;


/**
 * Класс контроллера для управления объектами ItemRequest в приложении ShareIt.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";

    @Qualifier("itemRequestServiceImpl")
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    /**
     * Получение списка запросов, созданных другими пользователями.
     *
     * @param userId Идентификатор пользователя.
     * @return Список вещей пользователя с указанным идентификатором.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemRequestDto> getAllItemRequestsFromUser(@RequestHeader(USER_ID_REQUEST_HEADER) Long userId) {
        log.info("Получение списка всех запросов пользователя с id: {}", userId);
        return itemRequestService.getAllItemRequestsByUserId(userId);
    }

    /**
     * Получение списка запросов, созданных другими пользователями.
     *
     * @param userId   Идентификатор пользователя, который будет использоваться для фильтрации запросов.
     * @param pageNum  Номер страницы, с которой нужно начать вывод данных (необязательный параметр).
     * @param pageSize Количество элементов на странице (необязательный параметр).
     * @return Список запросов вещей других пользователей в формате Dto.
     */
    @GetMapping(path = "/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                               @RequestParam(name = "from", required = false) Integer pageNum,
                                               @RequestParam(name = "size", required = false) Integer pageSize) {
        return itemRequestService.getAllRequests(userId, pageNum, pageSize);
    }

    /**
     * Обрабатывает POST-запрос на добавление запроса вещи.
     *
     * @param itemRequestDto Запрос, который нужно добавить.
     * @return Добавленный запрос вещи.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader(USER_ID_REQUEST_HEADER) Long userId,
                                 @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Добавление запроса вещи пользователем с id = " + userId);
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    /**
     * Обрабатывает GET-запрос на получение запроса вещи по идентификатору.
     *
     * @param itemRequestId Идентификатор запроса вещи.
     * @return Запрос вещи с указанным идентификатором.
     */
    @GetMapping("/{itemRequestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getItemRequestById(@PathVariable("itemRequestId") Long itemRequestId,
                                             @RequestHeader(USER_ID_REQUEST_HEADER) Long userId) {
        log.info("Получение запроса вещи с id = " + itemRequestId);
        return itemRequestService.getItemRequestById(itemRequestId, userId);
    }
}