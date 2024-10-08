package ru.practicum.shareit.server.request.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Класс представляет модель данных для объекта ItemRequest.
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {
    /**
     * Уникальный идентификатор запроса вещи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Содержание запроса вещи.
     */
    @Column(name = "description", nullable = false)
    private String description;
    /**
     * Идентификатор пользователя, создавшего запрос на вещь.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_id")
    private User requester;
    /**
     * Дата и время создания запроса.
     */
    @Column(name = "created", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;
    /**
     * Список ответов в формате: id вещи, название, id владельца.
     */
    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private Collection<Item> items = new ArrayList<>();
}