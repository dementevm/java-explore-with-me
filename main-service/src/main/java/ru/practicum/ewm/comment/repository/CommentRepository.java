package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.comment.model.Comment;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByEventId(Long eventId, Pageable pageable);

    Page<Comment> findAllByAuthorId(Long authorId, Pageable pageable);

    Optional<Comment> findByIdAndEventId(Long id, Long eventId);

    Optional<Comment> findByIdAndAuthorId(Long id, Long authorId);
}
