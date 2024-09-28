package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Collection<Comment> findAllByItemId(Long itemId, Sort sort);
}