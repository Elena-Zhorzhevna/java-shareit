package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.user.controller.UserController;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void findAllUsers() {
        Collection<User> expectedUsers = new ArrayList<>();

        expectedUsers.add(new User(1L, "NewUserName", "NewUser@mail.ru"));
        List<UserDto> expectedUserDtos = expectedUsers
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
        when(userService.getAllUsers()).thenReturn(expectedUserDtos);

        Collection<UserDto> actualUserDtos = userController.findAll();

        assertEquals(expectedUserDtos, actualUserDtos);
    }

    @Test
    void findUserByIdThenUserDtoReturned() {
        User expectedUser = new User(2L, "TestUserByIdName", "userById@mail.ru");
        UserDto expectedUserDto = UserMapper.mapToUserDto(expectedUser);
        when(userService.getUserById(anyLong())).thenReturn(expectedUserDto);

        UserDto actualUserDto = userController.getUserById(2L);

        assertEquals(expectedUserDto, actualUserDto);
    }


    @Test
    void createNewUserThenNewUserDtoReturned() {
        User userToSave = new User(1L, "UserName", "user@mail.ru");
        when(userService.addUser(userToSave)).thenReturn(UserMapper.mapToUserDto(userToSave));

        User actualUser = UserMapper.mapUserDtoToUser(userController.create(userToSave));

        assertEquals(userToSave, actualUser);
    }


    @Test
    void updateUserThenReturnUpdatedUserDto() {
        User userForUpdating = new User(3L, "OldUser", "oldUser@email.ru");
        userService.addUser(userForUpdating);
        UserDto newUserDto = new UserDto(3L, "NewName", "newUser@email.ru");

        when(userService.updateUser(3L, newUserDto)).thenReturn(UserMapper.mapToUserDto(userForUpdating));

        UserDto updatedUser = userController.updateUser(3L, newUserDto);

        assertEquals(updatedUser.getName(), userForUpdating.getName());
        assertEquals(updatedUser.getEmail(), userForUpdating.getEmail());
    }
}
