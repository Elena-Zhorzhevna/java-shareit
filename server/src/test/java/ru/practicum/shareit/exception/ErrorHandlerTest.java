package ru.practicum.shareit.exception;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.exception.ErrorHandler;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.user.controller.UserController;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;


@WebMvcTest(controllers = {UserController.class, ErrorHandler.class})
class ErrorHandlerTest {

    private static final String API_PREFIX = "/users";

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void handleNotFound_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        when(userService.getUserById(1L)).thenThrow(new NotFoundException("User not found"));

        mvc.perform(get(API_PREFIX + "/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Искомый объект не найден."))
                .andExpect(jsonPath("$.description").value("User not found"));
    }

    @Test
    void handleValidationError_ReturnsBadRequest_WhenValidationFails() throws Exception {
        User user = new User(null, "", "invalid-email");
        when(userService.addUser(any(User.class))).thenThrow(new ValidationException("Invalid user data"));

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации."))
                .andExpect(jsonPath("$.description").value("Invalid user data"));
    }

    @Test
    void handleInternalError_ReturnsInternalServerError() throws Exception {
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Internal server error"));

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Возникло исключение."))
                .andExpect(jsonPath("$.description").value("Internal server error"));
    }

    @Test
    @SneakyThrows
    void findAll_ReturnsEmptyList_WhenNoUsersExist() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @SneakyThrows
    void findAll_ReturnsUserList_WhenUsersExist() {
        UserDto user1 = new UserDto(1L, "User 1", "user1@example.com");
        UserDto user2 = new UserDto(2L, "User 2", "user2@example.com");
        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].name").value("User 2"));
    }

    @Test
    @SneakyThrows
    void findAll_HandlesServiceException_WhenUserServiceFails() {
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Service error"));

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Возникло исключение."))
                .andExpect(jsonPath("$.description").value(containsString("Service error")));
    }
}
