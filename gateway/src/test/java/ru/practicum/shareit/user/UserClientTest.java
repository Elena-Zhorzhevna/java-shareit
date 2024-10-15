package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * Класс для тестирования класса UserClient.
 */
@RestClientTest(UserClient.class)
class UserClientTest {
    private final String serverUrl = "http://localhost:9090/users";

    @Autowired
    private UserClient userClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private String body;
    private UserDto newUserDto;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        newUserDto = new UserDto(1L, "name", "email@email.com");
        body = objectMapper.writeValueAsString(newUserDto);
    }

    @Test
    @SneakyThrows
    void create() {
        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = userClient.create(newUserDto);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(body, responseBody);
    }

    @Test
    @SneakyThrows
    void getAll() {
        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body("[" + body + "]")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = userClient.getAllUsers();
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("[" + body + "]", responseBody);
    }

    @Test
    @SneakyThrows
    void getById() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/" + userId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = userClient.getById(userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(body, responseBody);
    }

    @Test
    @SneakyThrows
    void update() {
        Long userId = 1L;
        UserDto updateUserDto = new UserDto(2L, "name2", "email2@email.com");

        mockServer.expect(requestTo(serverUrl + "/" + userId))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = userClient.update(userId, updateUserDto);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(body, responseBody);
    }

    @Test
    @SneakyThrows
    void delete() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/" + userId))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        ResponseEntity<Object> responseEntity = userClient.delete(userId);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @SneakyThrows
    void create_whenBadRequest_thenReturnError() {
        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body("{\"error\":\"Некорректный тип запроса\", \"message\":\"Некорректный email\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = userClient.create(newUserDto);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @SneakyThrows
    void getById_whenNotFound_thenReturnError() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/" + userId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Пользователь не найден\", \"message\":\"Нет пользователя с таким id\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = userClient.getById(userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @SneakyThrows
    void update_whenValidationFails_thenReturnError() {
        Long userId = 1L;
        UserDto updateUserDto = new UserDto(userId, "", "invalid-email");

        mockServer.expect(requestTo(serverUrl + "/" + userId))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body("{\"error\":\"Ошибка валидации\", " +
                                "\"message\":\"name: Не должно быть пустым, email: Некорректный формат\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = userClient.update(userId, updateUserDto);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @SneakyThrows
    void delete_whenUserNotFound_thenReturnError() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/" + userId))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Пользователь не найден\", " +
                                "\"message\":\"Нет пользователя с таким id\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = userClient.delete(userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }
}