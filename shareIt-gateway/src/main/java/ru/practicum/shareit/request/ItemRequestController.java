package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Попытка создания запроса вещи пользователем с id = {}", userId);
        final ResponseEntity<Object> itemRequest = itemRequestClient.createItemRequest(userId, itemRequestDto);
        log.info("Создан запрос вещи пользователем с id = {}", userId);
        return itemRequest;
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Попытка получение запросов вещей пользователя с id = {}", userId);
        final ResponseEntity<Object> itemRequests = itemRequestClient.getAllItemRequestsByUserId(userId);
        log.info("Получен список запросов вещей пользователя с id = {}", userId);
        return itemRequests;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsByOtherUsers(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Попытка получения списка запросов, созданных другими пользователями.");
        final ResponseEntity<Object> itemRequests = itemRequestClient.getItemRequestsByOtherUsers(userId);
        log.info("Получен список запросов вещей всех пользователей, кроме пользователя с id = {}", userId);
        return itemRequests;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestsById(@PathVariable(name = "requestId") Long requestId) {
        log.info("Попытка получения запроса с id = {}", requestId);
        final ResponseEntity<Object> itemRequest = itemRequestClient.getItemRequestsById(requestId);
        log.info("Получен запрос с id = {}", requestId);
        return itemRequest;
    }
}