package ru.practicum.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CommentMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Comment toComment(NewCommentDto commentDto, User user, Event event) {
        return Comment.builder()
                .author(user)
                .event(event)
                .created(commentDto.getCreatedDate() != null
                        ? LocalDateTime.parse(commentDto.getCreatedDate(), FORMATTER) : LocalDateTime.now())
                .text(commentDto.getText())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(comment.getAuthor().getName())
                .text(comment.getText())
                .createdDate(comment.getCreated().format(FORMATTER))
                .build();
    }
}
