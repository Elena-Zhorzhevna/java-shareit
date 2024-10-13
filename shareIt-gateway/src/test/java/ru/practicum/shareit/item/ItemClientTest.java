package ru.practicum.shareit.item;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(ItemClient.class)
class ItemClientTest {
    private final String serverUrl = "http://localhost:9090/items";

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private final ItemDto newItemDto = new ItemDto("name", "description", true);
    private final ItemDto updateItemDto = new ItemDto("name2", "description2", true);
    private final CommentDto newCommentDto = new CommentDto("text");

    private String body;
    private String updateBody;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        body = objectMapper.writeValueAsString(newItemDto);
        updateBody = objectMapper.writeValueAsString(updateItemDto);
    }

    @Test
    @SneakyThrows
    void create() {
        Long ownerId = 1L;

        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemClient.createItem(ownerId, newItemDto);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(body, responseBody);
    }

    @Test
    @SneakyThrows
    void update() {
        Long userId = 1L;
        Long itemId = 1L;

        mockServer.expect(requestTo(serverUrl + "/%d".formatted(itemId)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(updateBody)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemClient.updateItem(itemId, userId, updateItemDto);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updateBody, responseBody);
    }

    @Test
    @SneakyThrows
    void getAllByUserId() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/%d".formatted(userId)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body("[" + body + "]") // Возвращаем массив
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemClient.getAllItemsByUserId(userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("[" + body + "]", responseBody);
    }

    @Test
    @SneakyThrows
    void getItemById() {
        Long userId = 1L;
        Long itemId = 1L;

        mockServer.expect(requestTo(serverUrl + "/%d".formatted(itemId)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemClient.getItemById(itemId, userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(body, responseBody);
    }

    @Test
    @SneakyThrows
    void getBySearch() {
        Long userId = 1L;
        String search = "name";

        mockServer.expect(requestTo(serverUrl + "/search?text=%s".formatted(search)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body("[" + body + "]") // Возвращаем массив
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemClient.getBySearch(userId, search);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("[" + body + "]", responseBody);
    }

    @Test
    @SneakyThrows
    void createComment() {
        Long userId = 1L;
        Long itemId = 1L;

        mockServer.expect(requestTo(serverUrl + "/%d/comment".formatted(itemId)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemClient.createComment(newCommentDto, itemId, userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(body, responseBody);
    }

    @Test
    @SneakyThrows
    void create_whenBadRequest_thenReturnError() {
        Long ownerId = 1L;

        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body("{\"error\":\"Некорректный запрос\", \"message\":\"Имя не должно быть пустым\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemClient.createItem(ownerId, newItemDto);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @SneakyThrows
    void update_whenItemNotFound_thenReturnError() {
        Long userId = 1L;
        Long itemId = 999L;

        mockServer.expect(requestTo(serverUrl + "/%d".formatted(itemId)))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Пользователь не найден\", \"message\":\"Нет такого предмета\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemClient.updateItem(itemId, userId, updateItemDto);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @SneakyThrows
    void createComment_whenCommentInvalid_thenReturnError() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto invalidCommentDto = new CommentDto(""); // Некорректный комментарий

        mockServer.expect(requestTo(serverUrl + "/%d/comment".formatted(itemId)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body("{\"error\":\"Ошибка валидации\", " +
                                "\"message\":\"Комментарий не может быть пустым\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemClient.createComment(invalidCommentDto, itemId, userId);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @SneakyThrows
    void getAllByUserId_whenNotFound_thenReturnError() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/%d".formatted(userId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Пользователь не найден\", \"message\":\"Нет элементов для данного пользователя\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemClient.getAllItemsByUserId(userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @SneakyThrows
    void getItemById_whenNotFound_thenReturnError() {
        Long userId = 1L;
        Long itemId = 999L;

        mockServer.expect(requestTo(serverUrl + "/%d".formatted(itemId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Пользователь не найден\", " +
                                "\"message\":\"Нет элемента с таким ID\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemClient.getItemById(itemId, userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @SneakyThrows
    void getBySearch_whenNoResults_thenReturnError() {
        Long userId = 1L;
        String search = "nonexistent";

        mockServer.expect(requestTo(serverUrl + "/search?text=%s".formatted(search)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Не найдено\", " +
                                "\"message\":\"Нет элементов по запросу\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemClient.getBySearch(userId, search);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

}