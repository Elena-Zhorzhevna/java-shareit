package ru.practicum.shareit.server.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.server.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b " +
            "where b.booker.id = ?1  " +
            "order by b.start desc")
    List<Booking> getAllByBookerId(Long bookerId);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and (b.start < current_timestamp and b.end > current_timestamp) " +
            "order by b.end desc")
    List<Booking> getAllCurrentByUserId(Long userId);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and (b.end < current_timestamp) " +
            "order by b.start desc")
    List<Booking> getAllPastByUserId(Long userId);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and (b.start > current_timestamp) " +
            "order by b.start desc")
    List<Booking> getAllFutureByUserId(Long userId);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status = 'WAITING' " +
            "order by b.end desc")
    List<Booking> getAllWaitingByUserId(Long userId);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status = 'REJECTED' " +
            "order by b.end desc")
    List<Booking> getAllRejectedByUserId(Long userId);

    @Query("select b from Booking b " +
            "where b.item.id in (?1)" +
            "order by b.end desc")
    List<Booking> getAllBookingsForItems(List<Long> itemIds);

    @Query("select b from Booking b where b.item.id in (?1)" +
            "and (b.start < current_timestamp and b.end > current_timestamp) " +
            "order by b.start desc")
    List<Booking> getCurrentBookingsForItems(List<Long> itemIds);

    @Query("select b from Booking b where b.item.id in (?1)" +
            "and (b.start < current_timestamp and b.end < current_timestamp) " +
            "order by b.start desc")
    List<Booking> getPastBookingsForItems(List<Long> itemIds);

    @Query("select b from Booking b where b.item.id in (?1)" +
            "and (b.start > current_timestamp and b.end > current_timestamp) " +
            "order by b.start desc")
    List<Booking> getFutureBookingsForItems(List<Long> itemIds);

    @Query("select b from Booking b " +
            "where b.item.id in (?1) and b.status = 'WAITING' " +
            "order by b.start desc")
    List<Booking> getWaitingBookingsForItems(List<Long> itemIds);

    @Query("select b from Booking b " +
            "where b.item.id in (?1) and b.status = 'REJECTED' " +
            "order by b.start desc")
    List<Booking> getRejectedBookingsForItems(List<Long> itemIds);

    @Query("select b from Booking b " +
            "where b.item.id in (?1) and b.status = 'REJECTED' " +
            "order by b.start desc")
    List<Booking> getCanceledBookingsForItems(List<Long> itemIds);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and b.start > current_timestamp " +
            "and b.status <> 'REJECTED' " +
            "order by b.start asc " +
            "limit 1")
    Booking getNextBookingForItem(Long itemId);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and b.start < current_timestamp " +
            "and b.status <> 'REJECTED' " +
            "order by b.start desc " +
            "limit 1")
    Booking getLastBookingForItem(Long itemId);
}