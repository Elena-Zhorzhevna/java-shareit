package ru.practicum.shareit.server.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;

/**
 * Класс представляет модель бронирования вещи.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    /**
     * Идентификатор бронирования
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Дата начала бронирования.
     */
    @Column(name = "start_date")
    private LocalDateTime start;
    /**
     * Дата окончания бронирования.
     */
    @Column(name = "end_date")
    private LocalDateTime end;
    /**
     * Вещь, которую бронирует пользователь.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    /**
     * Пользователь, бронирующий вещь.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;
    /**
     * Статус бронирования вещи.
     */
    @Enumerated(EnumType.STRING)
    private Status status;
}