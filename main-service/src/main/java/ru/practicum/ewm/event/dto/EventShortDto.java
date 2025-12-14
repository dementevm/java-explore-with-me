package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

public record EventShortDto(
        Long id,
        String annotation,
        CategoryDto category,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        UserShortDto initiator,
        Boolean paid,
        String title,
        Long confirmedRequests,
        Long views,
        List<CommentDto> comments
) {
}
