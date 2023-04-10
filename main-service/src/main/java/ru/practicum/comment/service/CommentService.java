package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ForbiddenRequestException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.service.ParticipationRequestService;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final EventService eventService;
    private final CommentMapper commentMapper;
    private final ParticipationRequestService requestService;


    public CommentDto addComment(long userId, long eventId, NewCommentDto commentDto) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        Comment comment = commentMapper.toComment(commentDto, user, event);
        checkNewComment(user, event, comment);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    public CommentDto getCommentById(long userId, long eventId, long comId) {
        userService.getUserById(userId);
        eventService.getEventById(eventId);
        Comment comment = commentRepository.findById(comId).orElseThrow(
                () -> new ObjectNotFoundException("Comment not found id=" + comId));
        return commentMapper.toCommentDto(comment);
    }

    public CommentDto updateComment(long userId, long eventId, long comId, UpdateCommentDto commentDto) {
        Comment comment = commentRepository.findById(comId).orElseThrow(
                () -> new ObjectNotFoundException("Comment not found id=" + comId));
        if (comment.getAuthor().getId() != userId) {
            throw new ForbiddenRequestException("User cannot change this comment");
        }
        if (comment.getEvent().getId() != eventId) {
            throw new ForbiddenRequestException("Comment refers to a different event.");
        }
        comment.setText(commentDto.getText());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    public void deleteCommentFromUserById(long userId, long eventId, long comId) {
        Comment comment = commentRepository.findById(comId).orElseThrow(
                () -> new ObjectNotFoundException("Comment not found id=" + comId));
        if (comment.getAuthor().getId() != userId) {
            throw new ForbiddenRequestException("User cannot delete this comment");
        }
        if (comment.getEvent().getId() != eventId) {
            throw new ForbiddenRequestException("Comment refers to a different event.");
        }
        commentRepository.deleteById(comId);
    }

    public void deleteCommentFromAdminById(long eventId, long comId) {
        Comment comment = commentRepository.findById(comId).orElseThrow(
                () -> new ObjectNotFoundException("Comment not found id=" + comId));
        if (comment.getEvent().getId() != eventId) {
            throw new ForbiddenRequestException("Comment refers to a different event.");
        }
        commentRepository.deleteById(comId);
    }

    private void checkNewComment(User user, Event event, Comment comment) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new ForbiddenRequestException("Event is not published");
        }
        if (user.getId() == event.getInitiator().getId()) {
            throw new ForbiddenRequestException("Initiator cannot write a comment");
        }
        if (comment.getCreated().isBefore(event.getEventDate())) {
            throw new ForbiddenRequestException("User cannot write a comment before event date");
        }
        List<ParticipationRequest> requests = requestService.getRequestByUserIdAndEventId(user.getId(), event.getId());
        if (requests.isEmpty()) {
            throw new ForbiddenRequestException("User cannot write a comment. He did not apply to participate in " +
                    "the event");
        }
    }
}
