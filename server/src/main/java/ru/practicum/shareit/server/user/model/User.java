package ru.practicum.shareit.server.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс представляет модель пользователя
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Имя или логин пользователя.
     */
    @Column(name = "name")
    private String name;
    /**
     * Адрес электронной почты пользователя /два пользователя не могут
     * иметь одинаковый адрес электронной почты/.
     */
    @Email
    @Column(name = "email", unique = true)
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}