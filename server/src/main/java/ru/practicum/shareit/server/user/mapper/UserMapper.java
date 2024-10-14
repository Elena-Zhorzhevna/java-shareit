package ru.practicum.shareit.server.user.mapper;

import lombok.Data;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.dto.UserDto;

/**
 * Класс для преобразования объектов типа User в тип UserDto и обратно.
 */
@Data
public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User mapUserDtoToUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail()).build();
    }
}