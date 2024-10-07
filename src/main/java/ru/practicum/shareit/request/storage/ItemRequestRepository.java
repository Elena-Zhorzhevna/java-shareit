package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> getAllByRequester_Id(Long userId);
    List<ItemRequest> getAllByRequesterIdOrderByCreatedDesc(Long userId);
    ItemRequest getItemRequestByIdOrderByCreatedAsc(Long itemRequestId);
    //List<ItemRequest> getAllByRequestId(Long itemRequestId, Sort sort);
}