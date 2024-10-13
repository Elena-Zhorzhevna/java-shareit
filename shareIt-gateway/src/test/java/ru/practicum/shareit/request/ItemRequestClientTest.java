package ru.practicum.shareit.request;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(ItemRequestClient.class)
class ItemRequestClientTest {
    private final String serverUrl = "http://localhost:9090/requests";

    @Autowired
    private ItemRequestClient itemRequestClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private String body;
    private ItemRequestDto newItemRequestDto;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        newItemRequestDto = new ItemRequestDto("description");
        body = objectMapper.writeValueAsString(newItemRequestDto);
    }

    @Test
    @SneakyThrows
    void createItemRequest() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemRequestClient.createItemRequest(userId, newItemRequestDto);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void getItemRequestsByUserId() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemRequestClient.getAllItemRequestsByUserId(userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void getItemRequestsByOtherUsers() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/all"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemRequestClient.getItemRequestsByOtherUsers(userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void getItemRequestById() {
        Long userId = 1L;
        Long requestId = 1L;

        mockServer.expect(requestTo(serverUrl + "/" + requestId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemRequestClient.getItemRequestById(requestId, userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void createItemRequest_whenBadRequest_thenReturnError() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body("{\"error\":\"Некорректный запрос\", " +
                                "\"message\":\"Описание не может быть пустым\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemRequestClient.createItemRequest(userId, newItemRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @SneakyThrows
    void getItemRequestById_whenNotFound_thenReturnError() {
        Long userId = 1L;
        Long requestId = 999L;

        mockServer.expect(requestTo(serverUrl + "/" + requestId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Запрос не найден\", \"message\":\"Нет такого запроса\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemRequestClient.getItemRequestById(requestId, userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @SneakyThrows
    void getItemRequestsByUserId_whenNotFound_thenReturnError() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Запросы не найдены\"," +
                                " \"message\":\"Нет запросов для данного пользователя\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemRequestClient.getAllItemRequestsByUserId(userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @SneakyThrows
    void getItemRequestsByOtherUsers_whenNotFound_thenReturnError() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/all"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Запросы не найдены\", " +
                                "\"message\":\"Нет запросов от других пользователей\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = itemRequestClient.getItemRequestsByOtherUsers(userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }
}