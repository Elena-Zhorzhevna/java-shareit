package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.mapper.CommentMapper;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class CommentMapperTest {

    @Test
    void mapToCommentDto_ShouldMapFieldsCorrectly() {

        User author = new User();
        author.setName("Author1");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment.");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        CommentDto commentDto = CommentMapper.mapToCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getItem(), commentDto.getItem());
        assertEquals(author.getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    @Test
    void mapCommentDtoToComment_ShouldMapFieldsCorrectly() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("This is a comment.");
        commentDto.setCreated(LocalDateTime.now());

        Item item = new Item();
        item.setId(1L);
        commentDto.setItem(item);

        Comment comment = CommentMapper.mapCommentDtoToComment(commentDto);

        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(commentDto.getItem(), comment.getItem());
        assertEquals(commentDto.getCreated(), comment.getCreated());
    }
}
