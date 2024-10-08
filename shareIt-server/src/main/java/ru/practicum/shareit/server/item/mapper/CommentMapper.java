package ru.practicum.shareit.server.item.mapper;

import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.model.Comment;

/**
 * Класс для преобразования объектов типа Comment в тип CommentDto и обратно.
 */
public class CommentMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(comment.getItem())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated()).build();
    }

    public static Comment mapCommentDtoToComment(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .item(commentDto.getItem())
                .created(commentDto.getCreated()).build();
    }
}