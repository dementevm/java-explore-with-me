package ru.practicum.ewm.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getEventComments(
            @PathVariable Long eventId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return commentService.getEventComments(eventId, from, size);
    }

    @GetMapping("/events/{eventId}/comments/{commentId}")
    public CommentDto getEventComment(
            @PathVariable Long eventId,
            @PathVariable Long commentId
    ) {
        return commentService.getEventComment(eventId, commentId);
    }

    @GetMapping("/users/{userId}/comments")
    public List<CommentDto> getUserComments(
            @PathVariable Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return commentService.getUserComments(userId, from, size);
    }

    @GetMapping("/users/{userId}/comments/{commentId}")
    public CommentDto getUserComment(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        return commentService.getUserComment(userId, commentId);
    }
}
