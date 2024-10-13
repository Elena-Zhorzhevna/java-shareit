package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserClient userClient;

    @SneakyThrows
    @Test
    void createUser_whenValidRequest_thenReturnStatusIsCreated() {
        UserDto userDto = new UserDto(1L, "John Doe", "john.doe@example.com");

        when(userClient.create(any(UserDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(userDto));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @SneakyThrows
    @Test
    void getUserById_whenValidRequest_thenReturnStatusIsOk() {
        UserDto userDto = new UserDto(1L, "John Doe", "john.doe@example.com");

        when(userClient.getById(1L))
                .thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(get("/users/{userId}", 1L))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenValidRequest_thenReturnStatusIsOk() {
        List<UserDto> userDtos = List.of(new UserDto(1L, "John Doe", "john.doe@example.com"));

        when(userClient.getAllUsers())
                .thenReturn(ResponseEntity.ok(userDtos));

        mockMvc.perform(get("/users"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
    }

    @SneakyThrows
    @Test
    void updateUser_whenValidRequest_thenReturnStatusIsOk() {
        UserDto userDto = new UserDto(1L, "John Doe", "john.doe@example.com");
        UserDto updatedUserDto = new UserDto(1L, "Jane Doe", "jane.doe@example.com");

        when(userClient.update(eq(1L), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok(updatedUserDto));

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserDto)))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
    }

    @SneakyThrows
    @Test
    void deleteUserById_whenValidRequest_thenReturnStatusIsOk() {
        when(userClient.delete(1L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/{userId}", 1L))
                //.andDo(print())
                .andExpect(status().isOk());
    }
}