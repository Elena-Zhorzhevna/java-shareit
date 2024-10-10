package ru.practicum.shareit.user;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;
import ru.practicum.shareit.server.user.storage.UserRepository;

import java.util.Collection;

/**
 * Интеграционный тестовый класс для проверки функциональности сервиса UserService.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testAddUser() {
        User user = new User();
        user.setName("TestUser");
        user.setEmail("test@email.ru");

        UserDto savedUser = userService.addUser(user);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("TestUser");
        assertThat(savedUser.getEmail()).isEqualTo("test@email.ru");
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setName("UserOne");
        user1.setEmail("userOne@email.com");

        User user2 = new User();
        user2.setName("UserTwo");
        user2.setEmail("userTwo@email.com");

        userRepository.save(user1);
        userRepository.save(user2);

        Collection<UserDto> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserDto::getName).containsExactlyInAnyOrder("UserOne", "UserTwo");
        assertThat(users).extracting(UserDto::getEmail).containsExactlyInAnyOrder("userOne@email.com",
                "userTwo@email.com");
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setName("TestUser");
        user.setEmail("test@email.ru");
        User savedUser = userRepository.save(user);

        UserDto foundUser = userService.getUserById(savedUser.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.getName()).isEqualTo("TestUser");
        assertThat(foundUser.getEmail()).isEqualTo("test@email.ru");
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setName("UserOne");
        user.setEmail("userOne@email.ru");
        User savedUser = userRepository.save(user);

        UserDto updateUserDto = new UserDto();
        updateUserDto.setId(savedUser.getId());
        updateUserDto.setName("UpdatedUser");
        updateUserDto.setEmail("updated@email.ru");

        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateUserDto);

        assertThat(updatedUser.getName()).isEqualTo("UpdatedUser");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@email.ru");
    }

    @Test
    public void testRemoveUserById() {
        User user = new User();
        user.setName("UserOne");
        user.setEmail("userOne@email.ru");
        User savedUser = userRepository.save(user);

        userService.removeUserById(savedUser.getId());

        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }

    @Test
    public void testRemoveAllUsers() {
        User user1 = new User();
        user1.setName("UserOne");
        user1.setEmail("userOne@email.ru");
        User user2 = new User();
        user2.setName("UserTwo");
        user2.setEmail("userTwo@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);

        userService.removeAllUsers();

        assertThat(userRepository.findAll()).isEmpty();
    }
}