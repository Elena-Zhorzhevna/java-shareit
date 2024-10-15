package ru.practicum.shareit.server.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByEmail(String email);
}