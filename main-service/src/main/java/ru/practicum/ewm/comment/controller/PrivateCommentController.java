package ru.practicum.ewm.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/comments")
public class PrivateCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getOwnComments(
            @RequestHeader("x-user-profile") Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return commentService.getPrivateComments(userId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getOwnComment(
            @RequestHeader("x-user-profile") Long userId,
            @PathVariable Long commentId
    ) {
        return commentService.getPrivateComment(userId, commentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(
            @RequestHeader("x-user-profile") Long userId,
            @RequestParam Long eventId,
            @Valid @RequestBody NewCommentDto newCommentDto
    ) {
        return commentService.createPrivateComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(
            @RequestHeader("x-user-profile") Long userId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentDto updateCommentDto
    ) {
        return commentService.updatePrivateComment(userId, commentId, updateCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @RequestHeader("x-user-profile") Long userId,
            @PathVariable Long commentId
    ) {
        commentService.deletePrivateComment(userId, commentId);
    }
}
