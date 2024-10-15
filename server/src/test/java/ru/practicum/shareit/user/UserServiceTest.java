package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.exception.ConflictException;
import ru.practicum.shareit.server.exception.NotFoundException;

import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserServiceImpl;
import ru.practicum.shareit.server.user.storage.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для проверки функциональности сервиса UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void testGetAllUsersReturnsUserDtoCollection() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("TestUserOne");
        user1.setEmail("one@email.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("TestUserTwo");
        user2.setEmail("two@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        Collection<UserDto> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(1L)));
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(2L)));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("TestUserOne")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("TestUserTwo")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("one@email.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("two@example.com")));
    }

    @Test
    void testAddUserWithUniqueEmailAndReturnUser() {
        User userToAdd = new User(4L, "AddingUserName", "addingUserEmail@mail.ru");
        when(userRepository.save(userToAdd)).thenReturn(userToAdd);

        User actualUser = UserMapper.mapUserDtoToUser(userService.addUser(userToAdd));

        assertEquals(userToAdd, actualUser);
        verify(userRepository, times(1)).save(userToAdd);
    }

    @Test
    public void testAddUserThrowsConflictExceptionWhenEmailExists() {

        String existingEmail = "test@example.com";
        User existingUser = new User();
        existingUser.setEmail(existingEmail);

        when(userRepository.findAll()).thenReturn(List.of(existingUser));

        User newUser = new User();
        newUser.setEmail(existingEmail);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            userService.addUser(newUser);
        });
        assertEquals("Пользователь с email: " + existingEmail + " уже существует.", exception.getMessage());
    }

    @Test
    public void testUpdateUserSuccessfully() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("OldUserName");
        existingUser.setEmail("oldUser@email.com");

        UserDto newUserDto = new UserDto();
        newUserDto.setName("NewUserName");
        newUserDto.setEmail("newUser@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto updatedUserDto = userService.updateUser(userId, newUserDto);

        assertEquals("NewUserName", updatedUserDto.getName());
        assertEquals("newUser@email.com", updatedUserDto.getEmail());
        verify(userRepository).save(existingUser);
    }

    @Test
    public void testUpdateUserThrowsNotFoundExceptionWhenUserDoesNotExist() {
        Long userId = 1L;
        UserDto newUserDto = new UserDto();
        newUserDto.setId(2L);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.updateUser(userId, newUserDto);
        });
        assertEquals("Пользователь с id = " + userId + " не найден!", exception.getMessage());
    }

    @Test
    public void testUpdateUserThrowsConflictExceptionWhenEmailExists() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("existing@email.ru");

        UserDto newUserDto = new UserDto();
        newUserDto.setEmail("existing@email.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        when(userRepository.findAll()).thenReturn(List.of(existingUser));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            userService.updateUser(userId, newUserDto);
        });
        assertEquals("Пользователь с email: existing@email.ru уже существует.", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    public void testGetUserByIdReturnsUserDtoWhenUserExists() {

        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("TestUserName");
        existingUser.setEmail("testUser@email.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        UserDto userDto = userService.getUserById(userId);

        assertNotNull(userDto);
        assertEquals(userId, userDto.getId());
        assertEquals("TestUserName", userDto.getName());
        assertEquals("testUser@email.ru", userDto.getEmail());
    }

    @Test
    public void testGetUserByIdThrowsNotFoundExceptionWhenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getUserById(userId);
        });
        assertEquals("Пользователь с id = " + userId + " не найден!", exception.getMessage());
    }

    @Test
    public void testRemoveAllUsersCallsDeleteAll() {
        userService.removeAllUsers();

        verify(userRepository, times(1)).deleteAll();
    }

    @Test
    public void testRemoveUserByIdSuccessfullyDeletesUser() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("TestUserOne");
        existingUser.setEmail("userOne@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        userService.removeUserById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    public void testRemoveUserByIdThrowsNotFoundExceptionWhenUserDoesNotExist() {
        Long userId = 3L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.removeUserById(userId);
        });
        assertEquals("Пользователь с id = " + userId + " не найден!", exception.getMessage());

        verify(userRepository, never()).deleteById(userId);
    }
}