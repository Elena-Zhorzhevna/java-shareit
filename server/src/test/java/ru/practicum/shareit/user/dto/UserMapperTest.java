package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.model.User;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тестовый класс для проверки функциональности класса UserMapper.
 */
public class UserMapperTest {

    @Test
    void mapToUserDtoFromUserTest() {
        User user = new User(2L, "TestUserName", "TestUserDescription");

        UserDto userDto = UserMapper.mapToUserDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void mapUserDtoToUserTest() {
        UserDto userDto = new UserDto(3L, "TestUserDtoName", "TestUserDtoDescription");

        User user = UserMapper.mapUserDtoToUser(userDto);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());

    }
}
