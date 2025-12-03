package ru.practicum.ewm.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin")
public class AdminCommentController {
    private final CommentService commentService;

    @PatchMapping("/events/{eventId}/comments/{commentId}")
    public CommentDto updateEventComment(
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentDto updateCommentDto
    ) {
        return commentService.updateEventComment(eventId, commentId, updateCommentDto);
    }

    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEventComment(
            @PathVariable Long eventId,
            @PathVariable Long commentId
    ) {
        commentService.deleteEventComment(eventId, commentId);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public CommentDto updateUserComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentDto updateCommentDto
    ) {
        return commentService.updateUserComment(userId, commentId, updateCommentDto);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserComment(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        commentService.deleteUserComment(userId, commentId);
    }
}
