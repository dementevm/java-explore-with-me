package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final EventService eventService;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    public Comment findByIdAndEvent(Long commentId, Long eventId) {
        return commentRepository.findByIdAndEventId(commentId, eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Comment with id %d not found".formatted(commentId)));
    }

    public Comment findByIdAndAuthor(Long commentId, Long authorId) {
        return commentRepository.findByIdAndAuthorId(commentId, authorId)
                .orElseThrow(() -> new ObjectNotFoundException("Comment with id %d not found".formatted(commentId)));
    }

    public List<CommentDto> getEventComments(Long eventId, int from, int size) {
        Pageable pageable = toPageable(from, size);
        return commentMapper.toDtoList(
                commentRepository.findAllByEventId(eventId, pageable).getContent()
        );
    }

    public CommentDto getEventComment(Long eventId, Long commentId) {
        Comment comment = findByIdAndEvent(commentId, eventId);
        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getUserComments(Long userId, int from, int size) {
        Pageable pageable = toPageable(from, size);
        return commentMapper.toDtoList(
                commentRepository.findAllByAuthorId(userId, pageable).getContent()
        );
    }

    public CommentDto getUserComment(Long userId, Long commentId) {
        Comment comment = findByIdAndAuthor(commentId, userId);
        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getPrivateComments(Long authorId, int from, int size) {
        Pageable pageable = toPageable(from, size);
        return commentMapper.toDtoList(
                commentRepository.findAllByAuthorId(authorId, pageable).getContent()
        );
    }

    public CommentDto getPrivateComment(Long authorId, Long commentId) {
        Comment comment = findByIdAndAuthor(commentId, authorId);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public CommentDto createPrivateComment(Long authorId, Long eventId, NewCommentDto newCommentDto) {
        User author = userMapper.toUser(userService.getUserById(authorId));
        Event event = eventMapper.toEvent(eventService.getPublishedEvent(eventId));
        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .author(author)
                .event(event)
                .createdOn(LocalDateTime.now())
                .build();
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updatePrivateComment(Long authorId, Long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = findByIdAndAuthor(commentId, authorId);
        comment.setText(updateCommentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public void deletePrivateComment(Long authorId, Long commentId) {
        Comment comment = findByIdAndAuthor(commentId, authorId);
        commentRepository.delete(comment);
    }

    @Transactional
    public CommentDto updateEventComment(Long eventId, Long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = findByIdAndEvent(commentId, eventId);
        comment.setText(updateCommentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteEventComment(Long eventId, Long commentId) {
        Comment comment = findByIdAndEvent(commentId, eventId);
        commentRepository.delete(comment);
    }

    @Transactional
    public CommentDto updateUserComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = findByIdAndAuthor(commentId, userId);
        comment.setText(updateCommentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteUserComment(Long userId, Long commentId) {
        Comment comment = findByIdAndAuthor(commentId, userId);
        commentRepository.delete(comment);
    }

    private Pageable toPageable(int from, int size) {
        int page = from / size;
        return PageRequest.of(page, size);
    }
}
