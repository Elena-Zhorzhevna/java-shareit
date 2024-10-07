package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId);

    void removeItemByOwnerId(Long ownerId);

    void removeItemByIdAndOwnerId(Long itemId, Long userId);

    List<Item> findAllByRequestId(Long requestId);

    @Query(" select i from Item i " +
            "where lower(i.name) like lower(concat('%', :search, '%')) " +
            " or lower(i.description) like lower(concat('%', :search, '%')) " +
            " and i.available = true")
    List<Item> getItemsBySearchQuery(@Param("search") String text);
}