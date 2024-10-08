package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String url, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createItem(Long ownerId, ItemDto ItemDto) {
        return post("", ownerId, ItemDto);
    }

    public ResponseEntity<Object> updateItem(Long itemId, Long userId, ItemDto newItemDto) {
        return patch("/%d".formatted(itemId), userId, newItemDto);
    }

    public ResponseEntity<Object> getAllItemsByUserId(Long userId) {
        return get("/%d".formatted(userId));
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get("/%d".formatted(itemId), userId);
    }

    public ResponseEntity<Object> getBySearch(Long userId, String text) {
        Map<String, Object> param = Map.of("text", text);
        return get("/search?text={text}", userId, param);
    }

    public ResponseEntity<Object> createComment(CommentDto createDto, Long userId, Long itemId) {
        return post("/" + itemId + "/comment", userId, createDto);
    }
}