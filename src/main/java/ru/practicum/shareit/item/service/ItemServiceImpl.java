package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Сервисный класс, который обрабатывает операции и взаимодействия, связанные с вещами.
 * Во всех случаях возвращает объекты ItemDto.
 */
@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestService requestService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, CommentRepository commentRepository,
                           UserService userService, UserRepository userRepository,
                           BookingRepository bookingRepository, ItemRequestService requestService) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.requestService = requestService;

    }

    /**
     * Получение всех вещей.
     *
     * @return Коллекция вещей.
     */
    @Override
    public Collection<ItemDto> getAll() {
        log.info("Запрос на получение всех вещей.");
        return itemRepository.findAll().stream()
                .map(ItemMapper::mapToItemDtoWithComments)
                .toList();
    }

    /**
     * Получение всех вещей владельца по его идентификатору.
     *
     * @param userId Идентификатор пользователя - владельца вещей.
     * @return Список вещей владельца.
     */
    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::mapToItemDtoWithComments)
                .collect(toList());
    }

    /**
     * Получение вещи по ее идентификатору.
     *
     * @param itemId Идентификатор вещи.
     * @return Вещь с указанным идентификатором.
     */
    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("Попытка получить вещь с id = {}", itemId);
        ItemDto dto = ItemMapper.mapToItemDtoWithComments(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена!")));
        dto.setComments(getCommentsByItemId(itemId));
        return dto;
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
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким Id " + userId + " не найден"));
        validateItemDto(itemDto);
        Item item = ItemMapper.mapItemDtoToItem(itemDto);

        item.setOwner(UserMapper.mapUserDtoToUser(userService.getUserById(userId)));
        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден!");
        }
        if (itemDto.getRequestId() != null) {
            requestService.addItemToRequest(itemDto);
        }

        ItemDto addingItem = ItemMapper.mapToItemDtoWithComments(itemRepository.save(item));
        addingItem.setComments(itemDto.getComments());
        return addingItem;
    }

    /**
     * Обновление вещи.
     *
     * @param userId     Идентификатор владельца.
     * @param itemId     Идентификатор обновляемой вещи.
     * @param newItemDto Вещь с обновленной информацией.
     * @return Обновленная вещь.
     */
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден."));

        Item oldItem = (itemRepository.findById(itemId)).get();

        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи.!");
        }

        if (newItemDto.getName() != null) {
            oldItem.setName(newItemDto.getName());
        }
        if (newItemDto.getDescription() != null) {
            oldItem.setDescription(newItemDto.getDescription());
        }
        if (newItemDto.getAvailable() != null) {
            oldItem.setAvailable(newItemDto.getAvailable());
        }

        ItemDto newDto = ItemMapper.mapToItemDtoWithComments(itemRepository.save(oldItem));
        List<CommentDto> commentDtos = getCommentsByItemId(newItemDto.getId());
        newDto.setComments(commentDtos);
        return newDto;
    }

    /**
     * Удаление всех вещей пользователя с указанным идентификатором.
     *
     * @param userId Идентификатор пользователя.
     */
    @Override
    public void removeAllItemsByOwnerId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.")
        );
        itemRepository.removeItemByOwnerId(userId);
    }

    /**
     * Удаление вещи по идентификатору.
     *
     * @param itemId Идентификатор удаляемой вещи.
     * @param userId Идентификатор пользователя - владельца вещи.
     */
    @Override
    public void removeItemById(Long itemId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.")
        );
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь id = " + itemId + " не найдена.")
        );
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Пользователь не является владельцем вещи.");
        }
        itemRepository.removeItemByIdAndOwnerId(itemId, userId);
    }

    /**
     * Создания отзыва для вещи.
     *
     * @param commentDto Отзыв в формате Дто.
     * @param itemId     Идентификатор вещи.
     * @param userId     Идентификатор пользователя.
     * @return Добавленный отзыв.
     */
    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден."));

        isItemBooker(userId, itemId);
        Item item = itemRepository.findById(itemId).get();
        if (commentDto == null) {
            throw new NotFoundException("Комментарий отсутствует");
        }
        if (commentDto.getText().isBlank()) {
            throw new NotFoundException("Текст комментария отсутствует.");
        }

        final Booking booking = bookingRepository.getAllByBookerId(userId).stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .filter(b -> b.getStatus().equals(Status.APPROVED))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Бронирование вещи не подверждено, " +
                        "нельзя добавить комментарий."));

        checkBookingEndTime(booking);

        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText(commentDto.getText());
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    /**
     * Метод получения комментариев вещи по ее идентификатору.
     *
     * @param itemId Идентификатор вещи.
     * @return Список комментариев в формате ДТО.
     */
    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItemId(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(toList());
    }

    /**
     * Метод для валидации объекта ItemDto
     *
     * @param itemDto Объект для проверки валидации.
     */
    private void validateItemDto(ItemDto itemDto) throws ValidationException {

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Отсутствует название вещи.");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Отсутствует описание вещи.");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Не указана доступность вещи для заказа.");
        }
    }

    /**
     * Метод для проверки, является ли пользователь арендатором вещи.
     *
     * @param userId Идентификатор пользователя.
     * @param itemId Идентификатор вещи.
     * @return Результат проверки true/false.
     */
    private Boolean isItemBooker(Long userId, Long itemId) {
        List<Booking> bookings = bookingRepository.getAllByBookerId(userId);
        if (bookings.isEmpty()) {
            return false;
        }
        for (Booking b : bookings) {
            if (b.getItem().getId().equals(itemId) && b.getStart().isBefore(LocalDateTime.now())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Метод для проверки факта, что бронирование вещи завершено на настоящий момент.
     *
     * @param booking Бронирование вещи.
     */
    private void checkBookingEndTime(Booking booking) {
        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Бронирование вещи еще не завершено.");
        }
    }
}