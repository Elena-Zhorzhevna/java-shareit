package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Сервисный класс, который обрабатывает операции и взаимодействия, связанные с запросами вещей.
 * Во всех случаях возвращает объекты ItemRequestDto.
 */
@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, UserService userService, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    /**
     * Получение всех запросов пользователя.
     *
     * @return Коллекция запросов.
     */
/*    @Override
    public Collection<ItemRequestDto> getAll() {
        log.info("Получение всех запросов пользователя.");
        return itemRequestRepository.findAll().stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
    }*/

    /**
     * Получение всех запросов пользователя, чей идентификатор указан.
     *
     * @param userId Идентификатор пользователя.
     * @return Список запросов пользователя.
     */
    @Override
    public List<ItemRequestDto> getAllItemRequestsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        return itemRequestRepository.getAllByRequester_Id(userId).stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(toList());
    }

    /**
     * Добавление запроса вещи пользователем с указанным идентификатором.
     *
     * @param userId         Идентификатор пользователя, добавляющего запрос на вещь.
     * @param itemRequestDto Запрос вещи в формате ДТО.
     * @return Добавленный запрос на вещь.
     */
    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        validateItemRequestDto(itemRequestDto);
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto, itemRequestDto.getRequester());
        itemRequest.setCreated(LocalDateTime.now());
        //itemRequest.setItems(itemRequestDto.getItems().stream().map(ItemMapper::mapItemNameDtoToItem).toList());
        itemRequest.setRequester(UserMapper.mapUserDtoToUser(userService.getUserById(userId)));
        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден!");
        }
        ItemRequestDto addingItemRequest = ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.save(itemRequest));

        addingItemRequest.setItems(itemRequestDto.getItems());
        return addingItemRequest;
    }

    @Override
    public ItemRequestDto getItemRequestById(Long itemRequestId, Long userId) {
        log.info("Попытка получить запрос вещи с id = {}", itemRequestId);

        userService.getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + itemRequestId + "не найден!"));
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }


    public List<ItemRequestDto> getAllRequests(Long userId, Integer pageNum, Integer pageSize) {
        userService.getUserById(userId);
        if (pageNum == null || pageSize == null) {
            log.info("Получение запросов вещи, если доп. параметры не указаны.");
            return itemRequestRepository.findAll().stream()
                    .filter(itemRequest -> !itemRequest.getRequester().getId().equals(userId))
                    .map(ItemRequestMapper::mapToItemRequestDto)
                    .collect(Collectors.toList());
        }
        validatePagesRequest(pageNum, pageSize);
        Pageable page = PageRequest.of(pageNum, pageSize);
        log.info("Получение запросов вещей с введенными параметрами.");
        return itemRequestRepository.findAll(page).stream()
                .filter(itemRequest -> !itemRequest.getRequester().getId().equals(userId))
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public void addItemToRequest(ItemDto itemDto) {
        log.info("Попытка добавить вещь в запрос с id = {}", itemDto.getRequestId());
        ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + itemDto.getRequestId() + "не найден!"));
        Collection<Item> requestItems = itemRequest.getItems();
        requestItems.add(ItemMapper.mapItemDtoToItem(itemDto));
        itemRequest.setItems(requestItems);
    }

    private void validatePagesRequest(Integer pageNum, Integer pageSize) {
        if (pageNum < 0 || pageSize <= 0) {
            String message = "Ошибка: неверно указано количество страниц или размер страницы.";
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    /**
     * Метод для валидации объекта ItemRequestDto.
     *
     * @param itemRequestDto Объект для проверки валидации.
     */
    private void validateItemRequestDto(ItemRequestDto itemRequestDto) throws ValidationException {

        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Отсутствует содержание запроса вещи.");
        }
    }
}