package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервисный класс, который обрабатывает операции и взаимодействия, связанные с вещами.
 * Во всех случаях возвращает объекты ItemDto.
 */
@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemStorage itemStorage;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(InMemoryItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    /**
     * Получение всех вещей.
     *
     * @return Коллекция вещей.
     */
    @Override
    public Collection<ItemDto> getAll() {
        log.info("Запрос на получение всех вещей.");
        return itemStorage.getAll().stream().map(ItemMapper::mapToItemDto).toList();
    }

    /**
     * Получение всех вещей владельца.
     *
     * @param userId Идентификатор пользователя - владельца вещей.
     * @return Список вещей владельца.
     */
    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        return itemStorage.findItemByOwnerId(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение вещи по идентификатору.
     *
     * @param id Идентификатор вещи.
     * @return Вещь с указанным идентификатором.
     */
    @Override
    public Optional<ItemDto> getItemById(Long id) {
        Item item = itemStorage.findItemById(id);
        return Optional.of(ItemMapper.mapToItemDto(item));
    }

    /**
     * Поиск вещи потенциальным арендатором.
     * Пользователь передаёт в строке запроса текст, и система ищет вещи,
     * содержащие этот текст в названии или описании.
     *
     * @param text Текст запроса.
     * @return Доступная для аренды вещь в формате Dto.
     */
    @Override
    public Collection<ItemDto> searchItemsByText(String text) {
        log.info("Запрос на поиск вещи, содержащий следующий текст: {}", text);
        if (text == null || text.isEmpty()) {
            log.warn("Текст для поиска отсутствует");
            return List.of();
        }
        List<ItemDto> itemsDto = getAll().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(ItemDto::getAvailable).toList();

        if (itemsDto.isEmpty()) {
            log.info("Вещь с запрашиваемым текстом {} отсутствует.", text);
            return itemsDto;
        }
        log.info("Получена коллекция вещей, где присутствует указанный текст {}.", text);
        return itemsDto;
    }

    /**
     * Добавление существующей вещи.
     *
     * @param itemDto Добавляемая вещь.
     * @return Добавленная вещь.
     */
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.mapItemDtoToItem(itemDto);
        userService.getUserById(userId);
        item.setOwner(userId);
        Item itemResult = itemStorage.create(userId, item);
        log.info("Добавлена вещь: \n{}", itemResult);
        ItemDto itemResultDto = ItemMapper.mapToItemDto(itemResult);
        log.info("Вещь в формате DTO: \n{}", itemResultDto);
        return itemResultDto;
    }

    /**
     * Обновление вещи.
     *
     * @param userId  Идентификатор владельца.
     * @param itemId  Идентификатор обновляемой вещи.
     * @param newItem Вещь с обновленной информацией.
     * @return Обновленная вещь.
     */
    @Override
    public ItemDto updateItem(Long userId, Long itemId, Item newItem) {
        return ItemMapper.mapToItemDto(itemStorage.update(userId, itemId, newItem));
    }

    /**
     * Удаление всех вещей пользователя с указанным идентификатором.
     *
     * @param userId Идентификатор пользователя.
     */
    @Override
    public void removeAllItemsByOwnerId(Long userId) {
        itemStorage.removeItemByOwnerId(userId);
    }

    /**
     * Удаление вещи по идентификатору.
     *
     * @param userId Идентификатор владельца вещи.
     * @param itemId Идентификатор удаляемой вещи.
     */
    @Override
    public void removeItemById(Long userId, Long itemId) {
        itemStorage.removeItemById(itemId);
    }
}